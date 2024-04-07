package kmp.shared.infrastructure.model

import kotlinx.serialization.Serializable

@Serializable
internal data class DistanceMatrixDto(
    val rows: List<Columns>
){
    @Serializable
    internal data class Columns(
        val elements: List<Elements>
    )

    @Serializable
    internal data class Elements(
        val distance: Distance,
        val duration: Duration
    )

    @Serializable
    internal data class Distance(
        val value: Int
    )

    @Serializable
    internal data class Duration(
        val value: Int
    )
}