package com.chelo.pokemon.feature.home.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chelo.pokemon.feature.home.ui.components.PokemonDetailContent
import com.chelo.pokemon.feature.home.ui.components.PokemonRow

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
        contentColor = Color.White,
        topBar = {
            if (inDetail) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black,
                        navigationIconContentColor = Color.Black
                    ),
                    title = { Text(text = state.selectedPokemon?.name ?: "Detalle") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                )
            } else {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black,
                        navigationIconContentColor = Color.Black
                    ),
                    title = { Text(text = "Pokédex") })
            }
        }
    ) { paddingValues ->
        val context = LocalContext.current
        AnimatedContent(
            modifier = Modifier.background(Color.White),
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
                    val (query, setQuery) = rememberSaveable { mutableStateOf("") }
                    val filtered = remember(query, state.pokemons) {
                        if (query.isBlank()) state.pokemons
                        else state.pokemons.filter { it.name.contains(query, ignoreCase = true) }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        AnimatedVisibility(visible = state.errorMessage != null) {
                            Surface(color = MaterialTheme.colorScheme.error) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = state.errorMessage ?: "",
                                        color = MaterialTheme.colorScheme.onError,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(onClick = { viewModel.dismissError() }) {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Cerrar",
                                            tint = MaterialTheme.colorScheme.onError
                                        )
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = query,
                            onValueChange = setQuery,
                            shape = RoundedCornerShape(100.dp),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                            placeholder = { Text("Buscar Pokémon") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .border(
                                    width = 0.5.dp,
                                    color = DarkGray,
                                    shape = RoundedCornerShape(100.dp)
                                ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color(0xFF646363),
                                focusedTextColor = DarkGray,
                                unfocusedTextColor = DarkGray,
                                focusedLabelColor = DarkGray,
                                unfocusedLabelColor = DarkGray,
                            )
                        )

                        if (query.isBlank() && state.recentSearches.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp)
                            ) {
                                Text(
                                    text = "Búsquedas recientes",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                                androidx.compose.foundation.layout.Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    state.recentSearches.take(6).forEach { recent ->
                                        AssistChip(
                                            onClick = { setQuery(recent) },
                                            label = { Text(recent) },
                                            colors = AssistChipDefaults.assistChipColors()
                                        )
                                    }
                                }
                            }
                        }

                        if (state.favoriteNames.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp)
                            ) {
                                Text(
                                    text = "Mis favoritos",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    state.favoriteNames.take(10).forEach { favName ->
                                        AssistChip(
                                            onClick = {
                                                viewModel.loadDetail(favName)
                                                onNavigateToDetail()
                                            },
                                            label = { Text(favName.replaceFirstChar { it.uppercase() }) },
                                            colors = AssistChipDefaults.assistChipColors()
                                        )
                                    }
                                }
                            }
                        }

                        if (filtered.isEmpty() && query.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Sin resultados",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier
                                    .fillMaxSize()
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
                                    items = filtered,
                                    key = { it.name }
                                ) { pokemon ->
                                    PokemonRow(
                                        pokemon = pokemon,
                                        onClick = {
                                            if (query.isNotBlank()) {
                                                viewModel.saveSearchQuery(query)
                                            }
                                            viewModel.loadDetail(pokemon.name)
                                            onNavigateToDetail()
                                        },
                                    )
                                }

                                if (state.hasNextPage && query.isBlank()) {
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
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(
                                                        24.dp
                                                    )
                                                )
                                            }
                                        }
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
                        AnimatedVisibility(visible = state.errorMessage != null) {
                            Surface(color = MaterialTheme.colorScheme.error) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = state.errorMessage ?: "",
                                        color = MaterialTheme.colorScheme.onError,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(onClick = { viewModel.dismissError() }) {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Cerrar",
                                            tint = MaterialTheme.colorScheme.onError
                                        )
                                    }
                                }
                            }
                        }
                        CircularProgressIndicator()
                    }
                }

                DetailState.CONTENT -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        AnimatedVisibility(visible = state.errorMessage != null) {
                            Surface(color = MaterialTheme.colorScheme.error) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = state.errorMessage ?: "",
                                        color = MaterialTheme.colorScheme.onError,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(onClick = { viewModel.dismissError() }) {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Cerrar",
                                            tint = MaterialTheme.colorScheme.onError
                                        )
                                    }
                                }
                            }
                        }
                        state.selectedPokemon?.let { pokemon ->
                            PokemonDetailContent(
                                pokemon = pokemon,
                                isFavorite = state.isFavoriteSelected,
                                onToggleFavorite = { viewModel.toggleFavoriteSelected() },
                                onShare = { text ->
                                    val sendIntent = android.content.Intent().apply {
                                        action = android.content.Intent.ACTION_SEND
                                        putExtra(android.content.Intent.EXTRA_TEXT, text)
                                        type = "text/plain"
                                    }
                                    val shareIntent = android.content.Intent.createChooser(sendIntent, null)
                                    context.startActivity(shareIntent)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        viewModel = hiltViewModel()
    )
}