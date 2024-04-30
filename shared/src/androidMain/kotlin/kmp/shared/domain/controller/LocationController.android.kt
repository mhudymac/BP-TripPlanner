package kmp.shared.domain.controller

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kmp.shared.domain.controller.LocationController.LocationUpdateCallback
import kmp.shared.domain.model.Location
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val UPDATE_INTERVAL = 10000L


internal actual class LocationController(
    private val context: Context,
    private val locationProvider: FusedLocationProviderClient,
) {
    fun interface LocationUpdateCallback {
        fun onLocationUpdate(location: Location)
    }

    private var listening: Boolean = false
    private val locationListeners: MutableList<LocationUpdateCallback> = mutableListOf()
    private var locationCallback: LocationCallback? = null

    private var lastLocation: Location? = null

    actual val locationFlow = callbackFlow {
        if (locationListeners.isEmpty() ||  !listening) {
            startListening()
        }
        this.trySend(lastLocation).isSuccess

        val callback = LocationUpdateCallback { location -> this.trySend(location).isSuccess }
        accessListeners { add(callback) }

        awaitClose {
            accessListeners { remove(callback) }
            if (locationListeners.isEmpty()) stopListening()
        }
    }.filterNotNull()

    @SuppressLint("MissingPermission")
        actual suspend fun getCurrentLocation(): Location? {
            if (permissionGranted) {
                return suspendCoroutine { continuation ->
                    val cancellationSource = CancellationTokenSource()
                    val token = cancellationSource.token

                    val request = CurrentLocationRequest.Builder()
                        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                        .build()

                    locationProvider.getCurrentLocation(request, token)
                        .addOnSuccessListener { location ->
                            cancellationSource.cancel()
                            location?.let { continuation.resume(Location(it.latitude, it.longitude)) }
                        }.addOnCanceledListener {
                            continuation.resume(null)
                        }
                }
            } else return null
        }

    @SuppressLint("MissingPermission")
    private fun startListening() {
        if (permissionGranted) {
            stopListening()
            listening = true

            locationProvider.lastLocation.addOnCompleteListener { task ->
                lastLocation = task.result?.let { Location(it.latitude, it.longitude) }
                lastLocation?.let { location ->
                    locationListeners.forEach { it.onLocationUpdate(location) }
                }
            }

            val request = LocationRequest.Builder(UPDATE_INTERVAL)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build()

            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    lastLocation = result.lastLocation.let { it?.let { it1 -> Location(it1.latitude, it.longitude) } }
                    lastLocation?.let { location ->
                        locationListeners.forEach { it.onLocationUpdate(location) }
                    }
                }
            }.also { locationCallback = it }

            locationProvider.requestLocationUpdates(
                request, callback, Looper.getMainLooper()
            )
        }
    }

    private fun stopListening() {
        locationCallback?.let(locationProvider::removeLocationUpdates)
        listening = false
    }

    private val permissionGranted
        get() = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION,) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION,) == PackageManager.PERMISSION_GRANTED
    /**
     * Helper function to prevent ConcurrentModification by synchronizing access to listeners
     */
    @Synchronized
    private fun accessListeners(block: MutableList<LocationUpdateCallback>.() -> Unit) =
        locationListeners.block()
}