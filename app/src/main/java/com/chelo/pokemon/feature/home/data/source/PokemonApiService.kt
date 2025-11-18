package com.chelo.pokemon.feature.home.data.source

import com.chelo.pokemon.feature.home.data.model.response.PokemonDetailResponseDto
import com.chelo.pokemon.feature.home.data.model.response.PokemonListResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonApiService {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonListResponseDto

    @GET("pokemon/{name}")
    suspend fun getPokemonDetail(
        @Path("name") name: String
    ): PokemonDetailResponseDto
}