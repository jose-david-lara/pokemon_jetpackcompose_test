package com.chelo.pokemon.feature.home.data.model.response

data class PokemonDetailResponseDto(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val sprites: SpritesDto,
    val types: List<PokemonTypeSlotDto>,
    val stats: List<PokemonStatDto>
)

data class SpritesDto(
    val front_default: String?
)

data class PokemonTypeSlotDto(
    val slot: Int,
    val type: NamedApiResourceDto
)

data class NamedApiResourceDto(
    val name: String,
    val url: String
)

data class PokemonStatDto(
    val base_stat: Int,
    val stat: NamedApiResourceDto
)
