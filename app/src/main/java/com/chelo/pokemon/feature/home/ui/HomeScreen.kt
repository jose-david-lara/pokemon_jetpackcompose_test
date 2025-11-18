package com.chelo.pokemon.feature.home.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.activity.compose.BackHandler
import com.chelo.pokemon.feature.home.ui.components.PokemonRow
import com.chelo.pokemon.feature.home.ui.components.PokemonDetailContent

private enum class DetailState {
    EMPTY, LOADING, CONTENT
}


@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (state.pokemons.isEmpty()) {
            viewModel.loadFirstPage()
        }
    }


    val currentDetailState = when {
        state.isDetailLoading -> DetailState.LOADING
        state.selectedPokemon != null -> DetailState.CONTENT
        else -> DetailState.EMPTY
    }

    val inDetail = currentDetailState != DetailState.EMPTY

    if (inDetail) {
        BackHandler {
            viewModel.clearSelection()
        }
    }

    Scaffold(
        topBar = {
            if (inDetail) {
                TopAppBar(
                    title = { Text(text = state.selectedPokemon?.name ?: "Detalle") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            } else {
                TopAppBar(title = { Text(text = "Pokédex") })
            }
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = currentDetailState,
            transitionSpec = {
                (fadeIn(animationSpec = tween(300)) +
                        slideInVertically { height -> height / 25 })
                    .togetherWith(
                        fadeOut(animationSpec = tween(200))
                    )
            },
            label = "ListDetailAnimation"
        ) { targetState ->
            when (targetState) {
                DetailState.EMPTY -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(8.dp)
                    ) {
                        if (state.errorMessage != null) {
                            item(span = { GridItemSpan(2) }) {
                                Text(
                                    text = state.errorMessage ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }

                        items(
                            items = state.pokemons,
                            key = { it.name }
                        ) { pokemon ->
                            PokemonRow(
                                pokemon = pokemon,
                                onClick = {
                                    viewModel.loadDetail(pokemon.name)
                                    onNavigateToDetail()
                                },
                            )
                        }

                        if (state.hasNextPage) {
                            item(span = { GridItemSpan(2) }) {
                                LaunchedEffect(state.nextOffset) {
                                    viewModel.loadNextPage()
                                }

                                AnimatedVisibility(
                                    visible = state.isLoading,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                DetailState.LOADING -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                DetailState.CONTENT -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        state.selectedPokemon?.let { pokemon ->
                            PokemonDetailContent(pokemon)
                        }
                    }
                }
            }
        }
    }
}
@Preview
@Composable
fun HomeScreenPreview(){
    HomeScreen(
        viewModel =  hiltViewModel()
    )
}