package com.igibeer.beerwall.data.remote.dto.places

data class Place(
    val id: String,
    val name: String,
    val city: String?,
    val hasFunds: Boolean
)

data class GetPlacesResponse(
    val places: List<Place>
)
