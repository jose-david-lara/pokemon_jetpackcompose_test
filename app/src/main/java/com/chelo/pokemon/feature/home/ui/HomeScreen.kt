package com.chelo.pokemon.feature.home.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chelo.pokemon.feature.home.ui.components.PokemonRow

private enum class DetailState {
    EMPTY, LOADING, CONTENT
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state = viewModel.uiState

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

    Row(Modifier.fillMaxSize()) {

        //Lista
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(
                items = state.pokemons,
                key = { it.name }
            ) { pokemon ->
                PokemonRow(
                    pokemon = pokemon,
                    onClick = { viewModel.loadDetail(pokemon.name) },
                )
            }

            item {
                if (state.hasNextPage) {
                    LaunchedEffect(state.nextOffset) {
                        viewModel.loadNextPage()
                    }
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

        //Detalle
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = currentDetailState,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(400)) +
                            slideInVertically { height -> height / 20 })
                        .togetherWith(
                            fadeOut(animationSpec = tween(200))
                        )
                },
                label = "DetailAnimation"
            ) { targetState ->
                when (targetState) {
                    DetailState.LOADING -> {
                        CircularProgressIndicator()
                    }
                    DetailState.CONTENT -> {
                        state.selectedPokemon?.let { pokemon ->
                            //PokemonDetailContent(pokemon)
                        }
                    }
                    DetailState.EMPTY -> {
                        Text(
                            text = "Seleccioná un Pokémon",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
@Preview
@Composable
fun HomeScreenPreview(){
    HomeScreen()
}