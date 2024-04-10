package kmp.android.shared.core.ui.util

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

abstract class PermissionRequest(
    protected open val launcher: ActivityResultLauncher<String>,
    open val granted: State<Boolean>,
) {
    abstract fun requestPermission()
}

@Composable
private fun <T : PermissionRequest> rememberPermissionRequest(
    factory: (
        launcher: ActivityResultLauncher<String>,
        granted: State<Boolean>,
    ) -> T,
): T {
    val granted = remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted.value = it },
    )

    return remember { factory(launcher, granted) }
}

// === Specific permissions ===

class PreciseLocationPermissionRequest(
    launcher: ActivityResultLauncher<String>,
    granted: State<Boolean>,
) : PermissionRequest(launcher, granted) {
    override fun requestPermission(): Unit =
        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION )
}

class CameraPermissionRequest(
    launcher: ActivityResultLauncher<String>,
    granted: State<Boolean>,
) : PermissionRequest(launcher, granted) {
    override fun requestPermission(): Unit =
        launcher.launch(Manifest.permission.CAMERA)
}

class GalleryPermissionRequest(
    launcher: ActivityResultLauncher<String>,
    granted: State<Boolean>,
) : PermissionRequest(launcher, granted) {
    override fun requestPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            launcher.launch(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

    }
}

@Composable
fun rememberPreciseLocationPermissionRequest(): PreciseLocationPermissionRequest =
    rememberPermissionRequest(::PreciseLocationPermissionRequest)

@Composable
fun rememberCameraPermissionRequest(): CameraPermissionRequest =
    rememberPermissionRequest(::CameraPermissionRequest)

@Composable
fun rememberGalleryPermissionRequest(): GalleryPermissionRequest =
    rememberPermissionRequest(::GalleryPermissionRequest)