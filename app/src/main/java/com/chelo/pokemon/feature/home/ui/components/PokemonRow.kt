package com.chelo.pokemon.feature.home.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.chelo.pokemon.feature.home.domain.entities.PokemonSummary

@Composable
fun PokemonRow(
    pokemon: PokemonSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.97f else 1f)
    val elevation by animateDpAsState(if (pressed) 8.dp else 2.dp)

    Card(
        modifier = modifier
            .graphicsLayer {
                this.scaleX = scale
                this.scaleY = scale
            }
            .clickable() {
                onClick()
            }
            .pointerInput(Unit) {
            }.background(Color.Transparent).padding(10.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(elevation)
    ) {
        Column(
            modifier = Modifier
                .padding(top = 24.dp, bottom = 12.dp, start = 10.dp, end = 12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(Modifier.width(12.dp))
            Text(
                text = pokemon.name,
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                modifier = Modifier.padding(start = 66.dp,  end = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                AsyncImage(
                    model = pokemon.imageUrl,
                    "Pokémon image",
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.Gray.copy(alpha = 0.1f))
                )

            }
        }
    }
}


@Preview
@Composable
fun PokemonRowPreview() {
    PokemonRow(
        pokemon = PokemonSummary(
            id = 1,
            name = "Bulbasaur",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"
        ),
        onClick = {}
    )
}