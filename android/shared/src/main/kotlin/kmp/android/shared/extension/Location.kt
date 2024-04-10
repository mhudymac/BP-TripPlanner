package kmp.android.shared.extension

import kmp.shared.domain.model.Location

fun Location.distanceTo(other: Location): Int {
    val distanceInMeters = FloatArray(1)

    android.location.Location.distanceBetween(
        latitude,
        longitude,
        other.latitude,
        other.longitude,
        distanceInMeters
    )

    return distanceInMeters[0].toInt()
}