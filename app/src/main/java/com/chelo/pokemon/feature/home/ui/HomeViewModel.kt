package com.chelo.pokemon.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chelo.pokemon.feature.home.domain.entities.PokemonDetail
import com.chelo.pokemon.feature.home.domain.entities.PokemonSummary
import com.chelo.pokemon.feature.home.domain.usecase.GetPokemonDetailUseCase
import com.chelo.pokemon.feature.home.domain.usecase.GetPokemonPageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
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
    private val _uiState = MutableStateFlow(PokemonUiState())
    val uiState: StateFlow<PokemonUiState> = _uiState

    private val pageSize = 10

    fun loadFirstPage() {
        if (_uiState.value.isLoading) return
        loadPage(offset = 0, isFirst = true)
    }

    fun loadNextPage() {
        val current = _uiState.value
        if (current.isLoading || !current.hasNextPage) return
        val nextOffset = current.nextOffset ?: return
        loadPage(offset = nextOffset, isFirst = false)
    }

    private fun loadPage(offset: Int, isFirst: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val page = getPokemonPage(limit = pageSize, offset = offset)

                val newList = if (isFirst) {
                    page.pokemons
                } else {
                    _uiState.value.pokemons + page.pokemons
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        pokemons = newList,
                        hasNextPage = page.hasNextPage,
                        nextOffset = page.nextOffset
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error cargando pokémon"
                    )
                }
            }
        }
    }

    fun loadDetail(name: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDetailLoading = true,
                    selectedPokemon = null,
                    errorMessage = null
                )
            }

            try {
                val detail = getPokemonDetail(name)
                _uiState.update {
                    it.copy(
                        isDetailLoading = false,
                        selectedPokemon = detail
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isDetailLoading = false,
                        errorMessage = e.message ?: "Error cargando detalle"
                    )
                }
            }
        }
    }

    fun clearSelection() {
        _uiState.update { it.copy(isDetailLoading = false, selectedPokemon = null) }
    }
}