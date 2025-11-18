package com.chelo.pokemon.feature.home.data.repository

import com.chelo.pokemon.feature.home.data.mapper.toDomain
import com.chelo.pokemon.feature.home.data.source.PokemonApiService
import com.chelo.pokemon.feature.home.data.source.PokemonLocalDataSource
import com.chelo.pokemon.feature.home.domain.entities.PokemonDetail
import com.chelo.pokemon.feature.home.domain.entities.PokemonPage
import com.chelo.pokemon.feature.home.domain.repository.PokemonRepository
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val api: PokemonApiService,
    private val localDataSource: PokemonLocalDataSource
) : PokemonRepository {

    override suspend fun getPokemonPage(limit: Int, offset: Int): PokemonPage {
        return try {
            val dto = api.getPokemonList(limit, offset)
            val page = dto.toDomain()
            localDataSource.savePokemonPage(limit, offset, page)

            page
        } catch (e: Exception) {
            val cached = localDataSource.getPokemonPage(limit, offset)
            if (cached != null) {
                cached
            } else {
                throw e
            }
        }
    }

    override suspend fun getPokemonDetail(name: String): PokemonDetail {
        return try {
            val dto = api.getPokemonDetail(name.lowercase())
            val detail = dto.toDomain()
            localDataSource.savePokemonDetail(detail)

            detail
        } catch (e: Exception) {
            val cached = localDataSource.getPokemonDetail(name)
            if (cached != null) {
                cached
            } else {
                throw e
            }
        }
    }
}