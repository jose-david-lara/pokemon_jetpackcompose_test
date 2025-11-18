package com.chelo.pokemon.feature.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.chelo.pokemon.core.theme.GrayApp
import com.chelo.pokemon.feature.home.domain.entities.PokemonDetail
import com.chelo.pokemon.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PokemonDetailContent(
    pokemon: PokemonDetail,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onShare: (String) -> Unit,
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("About", "Base Stats")

    Column(
        modifier = Modifier
            .fillMaxSize()

            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = 800f
                )
            )
    ) {



        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )
        {
            Text(
                text = pokemon.name,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(Modifier.height(8.dp))


            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pokemon.types.forEach { type ->
                    Surface(
                        color = Color.White.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(50),
                    ) {
                        Text(
                            text = type,
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "#${pokemon.id.toString().padStart(4, '0')}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ElevatedButton(onClick = {
                    val shareText = buildString {
                        append("${pokemon.name} (id ${pokemon.id})\n")
                        append(pokemon.imageUrls.firstOrNull() ?: pokemon.imageUrl)
                        append("\nhttps://pokeapi.co/api/v2/pokemon/${pokemon.name.lowercase()}")
                    }
                    onShare(shareText)
                }) {
                    Text("Compartir")
                }

                ElevatedButton(onClick = onToggleFavorite) {
                    Text(if (isFavorite) "Eliminar de mis favoritos" else "Agregar a favoritos")
                }
            }
        }

        Spacer(Modifier.height(12.dp))


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            val pagerState = rememberPagerState(pageCount = { maxOf(1, pokemon.imageUrls.size) })
            HorizontalPager(state = pagerState) { page ->
                val url = pokemon.imageUrls.getOrNull(page) ?: pokemon.imageUrl
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(200.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))


        Box(
            modifier = Modifier.weight(1f)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent),
                shape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    // Tabs
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = GrayApp.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.primary,
                        divider = {
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = Color.Transparent
                            )
                        },
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            )
                        }
                    }


                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .background(color = GrayApp.copy(alpha = 0.1f))
                            .padding(24.dp)
                    ) {
                        when (selectedTab) {
                            0 -> AboutTab(pokemon)
                            1 -> BaseStatsTab(pokemon)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AboutTab(pokemon: PokemonDetail) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        if (!pokemon.description.isNullOrBlank()) {
            Text(
                text = pokemon.description ?: "",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        InfoRow(label = "Species", value = "Seed")
        InfoRow(label = "Height", value = "${pokemon.height / 10f} m (${pokemon.height})")
        InfoRow(label = "Weight", value = "${pokemon.weight / 10f} kg (${pokemon.weight})")

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Breeding",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        InfoRow(label = "Gender", value = "♂ 87.5%  ♀ 12.5%")
        InfoRow(label = "Egg Groups", value = "Monster")
        InfoRow(label = "Egg Cycle", value = "Grass")
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1.5f)
        )
    }
}

@Composable
fun BaseStatsTab(pokemon: PokemonDetail) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        pokemon.baseStats.entries.forEach { (stat, value) ->
            StatRow(stat = stat, value = value, maxValue = 255)
        }
    }
}

@Composable
fun StatRow(stat: String, value: Int, maxValue: Int = 255) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stat.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.width(80.dp)
        )

        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.End
        )

        Spacer(Modifier.width(16.dp))

        LinearProgressIndicator(
            progress = { value.toFloat() / maxValue },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = when {
                value >= 150 -> Color(0xFF4CAF50)
                value >= 100 -> Color(0xFFFFC107)
                value >= 50 -> Color(0xFFFF9800)
                else -> Color(0xFFF44336)
            },
            trackColor = Color.LightGray.copy(alpha = 0.3f)
        )
    }
}


@Composable
private fun StatRow(stat: String, value: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stat.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodyMedium)
        Text(text = value.toString(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Preview
@Composable
fun PokemonDetailContentPreview() {
    val samplePokemon = PokemonDetail(
        id = 1,
        name = "Bulbasaur",
        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
        imageUrls = listOf(
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png"
        ),
        types = listOf("Grass", "Poison"),
        height = 7,
        weight = 69,
        description = "A strange seed was planted on its back at birth. The plant sprouts and grows with this Pokémon.",
        baseStats = mapOf(
            "hp" to 45,
            "attack" to 49,
            "defense" to 49,
            "special-attack" to 65,
            "special-defense" to 65,
            "speed" to 45
        )
    )

    Box(
        modifier = Modifier.background(Color.White)
    ) {
        PokemonDetailContent(
            pokemon = samplePokemon,
            isFavorite = true,
            onToggleFavorite = {},
            onShare = {}
        )
    }
}