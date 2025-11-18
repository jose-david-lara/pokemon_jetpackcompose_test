package com.chelo.pokemon.feature.home.domain.usecase

import com.chelo.pokemon.feature.home.domain.entities.PokemonPage
import com.chelo.pokemon.feature.home.domain.entities.PokemonSummary
import com.chelo.pokemon.feature.home.domain.repository.PokemonRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetPokemonPageUseCaseTest {

    private lateinit var repository: PokemonRepository
    private lateinit var useCase: GetPokemonPageUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetPokemonPageUseCase(repository)
    }

    @Test
    fun `invoke returns page from repository with given params`() = runTest {
        val page = PokemonPage(
            pokemons = listOf(
                PokemonSummary(1, "Bulbasaur", "url1"),
                PokemonSummary(2, "Ivysaur", "url2"),
            ),
            hasNextPage = true,
            nextOffset = 20
        )
        coEvery { repository.getPokemonPage(limit = 20, offset = 0) } returns page

        val result = useCase(limit = 20, offset = 0)
        assertEquals(page, result)
        coVerify(exactly = 1) { repository.getPokemonPage(20, 0) }
    }
}
