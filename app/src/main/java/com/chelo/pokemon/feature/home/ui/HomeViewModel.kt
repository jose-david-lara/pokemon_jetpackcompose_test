package com.chelo.pokemon.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chelo.pokemon.feature.home.domain.entities.PokemonDetail
import com.chelo.pokemon.feature.home.domain.entities.PokemonSummary
import com.chelo.pokemon.feature.home.domain.usecase.GetPokemonDetailUseCase
import com.chelo.pokemon.feature.home.domain.usecase.GetPokemonPageUseCase
import com.chelo.pokemon.feature.home.data.source.PokemonLocalDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import retrofit2.HttpException


data class PokemonUiState(
    val isLoading: Boolean = false,
    val pokemons: List<PokemonSummary> = emptyList(),
    val hasNextPage: Boolean = false,
    val nextOffset: Int? = null,
    val isDetailLoading: Boolean = false,
    val selectedPokemon: PokemonDetail? = null,
    val isFavoriteSelected: Boolean = false,
    val recentSearches: List<String> = emptyList(),
    val favoriteNames: Set<String> = emptySet(),
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPokemonPage: GetPokemonPageUseCase,
    private val getPokemonDetail: GetPokemonDetailUseCase,
    private val local: PokemonLocalDataSource
) : ViewModel() {
    private val _uiState = MutableStateFlow(PokemonUiState())
    val uiState: StateFlow<PokemonUiState> = _uiState

    private val pageSize = 10

    init {
        loadRecentSearches()
        loadFavoriteNames()
    }

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
                val msg = when (e) {
                    is HttpException -> when (e.code()) {
                        400 -> "Solicitud inválida (400)"
                        500 -> "Error del servidor (500)"
                        else -> "Error HTTP ${e.code()}"
                    }
                    else -> e.message ?: "Error cargando pokémon"
                }
                _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
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
                        selectedPokemon = detail,
                        isFavoriteSelected = local.isFavorite(detail.name)
                    )
                }
            } catch (e: Exception) {
                val msg = when (e) {
                    is HttpException -> when (e.code()) {
                        400 -> "Solicitud inválida (400)"
                        500 -> "Error del servidor (500)"
                        else -> "Error HTTP ${e.code()}"
                    }
                    else -> e.message ?: "Error cargando detalle"
                }
                _uiState.update { it.copy(isDetailLoading = false, errorMessage = msg) }
            }
        }
    }

    fun clearSelection() {
        _uiState.update { it.copy(isDetailLoading = false, selectedPokemon = null, isFavoriteSelected = false) }
    }

    fun toggleFavoriteSelected() {
        val current = _uiState.value.selectedPokemon ?: return
        val nowFav = !_uiState.value.isFavoriteSelected
        if (nowFav) local.addFavorite(current.name) else local.removeFavorite(current.name)
        _uiState.update { it.copy(isFavoriteSelected = nowFav, favoriteNames = local.getFavorites()) }
    }

    fun saveSearchQuery(query: String) {
        viewModelScope.launch {
            local.saveSearchQuery(query)
            loadRecentSearches()
        }
    }

    fun loadRecentSearches() {
        _uiState.update { it.copy(recentSearches = local.getRecentSearches()) }
    }

    fun loadFavoriteNames() {
        _uiState.update { it.copy(favoriteNames = local.getFavorites()) }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}