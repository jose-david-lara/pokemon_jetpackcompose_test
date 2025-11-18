package com.chelo.pokemon.feature.home.data.model.response

data class PokemonListResponseDto(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonResultDto>
)

data class PokemonResultDto(
    val name: String,
    val url: String
)
