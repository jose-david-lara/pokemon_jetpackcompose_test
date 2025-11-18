package com.chelo.pokemon.feature.home.domain.usecase

import com.chelo.pokemon.feature.home.domain.entities.PokemonDetail
import com.chelo.pokemon.feature.home.domain.repository.PokemonRepository
import javax.inject.Inject

class GetPokemonDetailUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(name: String): PokemonDetail =
        repository.getPokemonDetail(name)
}