package com.chelo.pokemon.feature.home.domain.usecase

import com.chelo.pokemon.feature.home.domain.entities.PokemonDetail
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
class GetPokemonDetailUseCaseTest {

    private lateinit var repository: PokemonRepository
    private lateinit var useCase: GetPokemonDetailUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetPokemonDetailUseCase(repository)
    }

    @Test
    fun `invoke returns detail from repository`() = runTest {
        val detail = PokemonDetail(
            id = 1,
            name = "Bulbasaur",
            imageUrl = "url",
            height = 7,
            weight = 69,
            types = listOf("grass"),
            baseStats = mapOf("hp" to 45)
        )
        coEvery { repository.getPokemonDetail("bulbasaur") } returns detail

        val result = useCase("bulbasaur")
        assertEquals(detail, result)
        coVerify(exactly = 1) { repository.getPokemonDetail("bulbasaur") }
    }
}
