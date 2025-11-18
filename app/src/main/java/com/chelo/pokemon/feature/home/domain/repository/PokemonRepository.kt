package com.chelo.pokemon.feature.home.domain.repository

import com.chelo.pokemon.feature.home.domain.entities.PokemonDetail
import com.chelo.pokemon.feature.home.domain.entities.PokemonPage

interface PokemonRepository {
    suspend fun getPokemonPage(limit: Int,offset: Int): PokemonPage
    suspend fun getPokemonDetail(name: String): PokemonDetail
}