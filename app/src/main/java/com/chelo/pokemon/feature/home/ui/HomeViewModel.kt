package com.chelo.pokemon.feature.home.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chelo.pokemon.feature.home.domain.entities.PokemonDetail
import com.chelo.pokemon.feature.home.domain.entities.PokemonSummary
import com.chelo.pokemon.feature.home.domain.usecase.GetPokemonDetailUseCase
import com.chelo.pokemon.feature.home.domain.usecase.GetPokemonPageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


data class PokemonUiState(
    val isLoading: Boolean = false,
    val pokemons: List<PokemonSummary> = emptyList(),
    val hasNextPage: Boolean = false,
    val nextOffset: Int? = null,
    // Detalle
    val isDetailLoading: Boolean = false,
    val selectedPokemon: PokemonDetail? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPokemonPage: GetPokemonPageUseCase,
    private val getPokemonDetail: GetPokemonDetailUseCase
) : ViewModel() {

    var uiState by mutableStateOf(PokemonUiState())
        private set

    private val pageSize = 10

    fun loadFirstPage() {
        if (uiState.isLoading) return
        loadPage(offset = 0, isFirst = true)
    }

    fun loadNextPage() {
        if (uiState.isLoading || !uiState.hasNextPage) return
        val nextOffset = uiState.nextOffset ?: return
        loadPage(offset = nextOffset, isFirst = false)
    }

    private fun loadPage(offset: Int, isFirst: Boolean) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            try {
                val page = getPokemonPage(limit = pageSize, offset = offset)

                val newList = if (isFirst) {
                    page.pokemons
                } else {
                    uiState.pokemons + page.pokemons
                }

                uiState = uiState.copy(
                    isLoading = false,
                    pokemons = newList,
                    hasNextPage = page.hasNextPage,
                    nextOffset = page.nextOffset
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error cargando pokémon"
                )
            }
        }
    }

    fun loadDetail(name: String) {
        viewModelScope.launch {
            uiState = uiState.copy(
                isDetailLoading = true,
                selectedPokemon = null,
                errorMessage = null
            )

            try {
                val detail = getPokemonDetail(name)
                uiState = uiState.copy(
                    isDetailLoading = false,
                    selectedPokemon = detail
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isDetailLoading = false,
                    errorMessage = e.message ?: "Error cargando detalle"
                )
            }
        }
    }
}