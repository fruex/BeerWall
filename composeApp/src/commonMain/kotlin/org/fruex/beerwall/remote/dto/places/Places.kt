package org.fruex.beerwall.remote.dto.places

data class Place(
    val id: String,
    val name: String,
    val city: String?,
)

data class GetPlacesResponse(
    val data: List<Place>
)
