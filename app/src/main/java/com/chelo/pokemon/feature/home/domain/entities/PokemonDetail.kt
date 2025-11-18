package com.chelo.pokemon.feature.home.domain.entities

data class PokemonDetail(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val height: Int,
    val weight: Int,
    val types: List<String>,
    val baseStats: Map<String, Int>
)

data class PokemonPage(
    val pokemons: List<PokemonSummary>,
    val hasNextPage: Boolean,
    val nextOffset: Int?
)
