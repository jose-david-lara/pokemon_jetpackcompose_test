package com.chelo.pokemon.feature.home.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chelo.pokemon.feature.home.domain.entities.PokemonSummary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PokemonList(
    pokemons: List<PokemonSummary>,
    onPokemonClick: (PokemonSummary) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = pokemons,
            key = { it.id }
        ) { pokemon ->
            var visible by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                visible = true
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 }),
                exit = fadeOut()
            ) {
                PokemonRow(
                    pokemon = pokemon,
                    onClick = { onPokemonClick(pokemon) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItemPlacement()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}
