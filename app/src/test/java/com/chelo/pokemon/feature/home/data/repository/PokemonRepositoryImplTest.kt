package com.chelo.pokemon.feature.home.data.repository

import com.chelo.pokemon.feature.home.data.model.response.NamedApiResourceDto
import com.chelo.pokemon.feature.home.data.model.response.PokemonDetailResponseDto
import com.chelo.pokemon.feature.home.data.model.response.PokemonListResponseDto
import com.chelo.pokemon.feature.home.data.model.response.PokemonResultDto
import com.chelo.pokemon.feature.home.data.model.response.PokemonStatDto
import com.chelo.pokemon.feature.home.data.model.response.PokemonTypeSlotDto
import com.chelo.pokemon.feature.home.data.model.response.SpritesDto
import com.chelo.pokemon.feature.home.data.source.PokemonApiService
import com.chelo.pokemon.feature.home.data.source.PokemonLocalDataSource
import com.chelo.pokemon.feature.home.domain.entities.PokemonDetail
import com.chelo.pokemon.feature.home.domain.entities.PokemonPage
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonRepositoryImplTest {

    private lateinit var api: PokemonApiService
    private lateinit var local: PokemonLocalDataSource
    private lateinit var repository: PokemonRepositoryImpl

    @Before
    fun setUp() {
        api = mockk()
        local = mockk()
        repository = PokemonRepositoryImpl(api, local)
    }

    @Test
    fun `getPokemonPage returns mapped page and caches it`() = runTest {
        val dto = PokemonListResponseDto(
            count = 2,
            next = "https://pokeapi.co/api/v2/pokemon?offset=20&limit=20",
            previous = null,
            results = listOf(
                PokemonResultDto(name = "bulbasaur", url = "https://pokeapi.co/api/v2/pokemon/1/"),
                PokemonResultDto(name = "ivysaur", url = "https://pokeapi.co/api/v2/pokemon/2/")
            )
        )
        coEvery { api.getPokemonList(limit = 20, offset = 0) } returns dto
        every { local.savePokemonPage(any(), any(), any()) } returns Unit

        val page: PokemonPage = repository.getPokemonPage(limit = 20, offset = 0)

        assertEquals(2, page.pokemons.size)
        assertTrue(page.hasNextPage)
        assertEquals(20, page.nextOffset)
        verify(exactly = 1) { local.savePokemonPage(20, 0, page) }
    }

    @Test
    fun `getPokemonPage uses cache on failure`() = runTest {
        val cached = PokemonPage(emptyList(), hasNextPage = false, nextOffset = null)
        coEvery { api.getPokemonList(limit = 20, offset = 0) } throws RuntimeException("network")
        every { local.getPokemonPage(20, 0) } returns cached

        val page = repository.getPokemonPage(20, 0)
        assertEquals(cached, page)
    }

    @Test
    fun `getPokemonDetail returns mapped detail and caches it`() = runTest {
        val dto = PokemonDetailResponseDto(
            id = 1,
            name = "bulbasaur",
            height = 7,
            weight = 69,
            sprites = SpritesDto(front_default = null),
            types = listOf(
                PokemonTypeSlotDto(slot = 1, type = NamedApiResourceDto(name = "grass", url = "")),
                PokemonTypeSlotDto(slot = 2, type = NamedApiResourceDto(name = "poison", url = ""))
            ),
            stats = listOf(
                PokemonStatDto(base_stat = 45, stat = NamedApiResourceDto(name = "hp", url = ""))
            )
        )
        coEvery { api.getPokemonDetail("bulbasaur") } returns dto
        every { local.savePokemonDetail(any()) } returns Unit

        val detail: PokemonDetail = repository.getPokemonDetail("Bulbasaur")

        assertEquals(1, detail.id)
        assertEquals("Bulbasaur", detail.name)
        assertTrue(detail.types.contains("grass"))
        verify(exactly = 1) { local.savePokemonDetail(detail) }
        assertNotNull(detail.imageUrl)
    }

    @Test
    fun `getPokemonDetail uses cache on failure`() = runTest {
        val cached = PokemonDetail(
            id = 1,
            name = "Bulbasaur",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
            imageUrls = listOf("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"),
            height = 7,
            weight = 69,
            description = null,
            types = listOf("grass"),
            baseStats = mapOf("hp" to 45)
        )
        coEvery { api.getPokemonDetail("bulbasaur") } throws RuntimeException("network")
        every { local.getPokemonDetail("Bulbasaur") } returns cached

        val result = repository.getPokemonDetail("Bulbasaur")
        assertEquals(cached, result)
    }
}
