package com.chelo.pokemon.feature.home.domain.usecase

import com.chelo.pokemon.feature.home.domain.entities.PokemonPage
import com.chelo.pokemon.feature.home.domain.repository.PokemonRepository
import javax.inject.Inject

class GetPokemonPageUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(limit: Int, offset: Int): PokemonPage =
        repository.getPokemonPage(limit, offset)
}