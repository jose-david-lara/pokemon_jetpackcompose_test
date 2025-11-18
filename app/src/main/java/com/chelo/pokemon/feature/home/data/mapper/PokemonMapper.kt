package com.chelo.pokemon.feature.home.data.mapper

import com.chelo.pokemon.feature.home.data.model.response.PokemonDetailResponseDto
import com.chelo.pokemon.feature.home.data.model.response.PokemonListResponseDto
import com.chelo.pokemon.feature.home.data.model.response.PokemonResultDto
import com.chelo.pokemon.feature.home.domain.entities.PokemonDetail
import com.chelo.pokemon.feature.home.domain.entities.PokemonPage
import com.chelo.pokemon.feature.home.domain.entities.PokemonSummary

fun PokemonListResponseDto.toDomain(): PokemonPage {
    val list = results.map { it.toDomain() }
    val nextOffset = next
        ?.substringAfter("offset=", missingDelimiterValue = "")
        ?.substringBefore("&", missingDelimiterValue = "")
        ?.toIntOrNull()

    return PokemonPage(
        pokemons = list,
        hasNextPage = next != null,
        nextOffset = nextOffset
    )
}

fun PokemonResultDto.toDomain(): PokemonSummary {
    val id = url.trimEnd('/').substringAfterLast("/").toInt()
    val imageUrl =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"

    return PokemonSummary(
        id = id,
        name = name.replaceFirstChar { it.uppercase() },
        imageUrl = imageUrl
    )
}

fun PokemonDetailResponseDto.toDomain(): PokemonDetail {
    val typesDomain = types
        .sortedBy { it.slot }
        .map { it.type.name }

    val statsMap = stats.associate { statDto ->
        statDto.stat.name to statDto.base_stat
    }

    val primaryImage = sprites.front_default
        ?: "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
    val imageList = listOf(
        primaryImage,
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png",
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${id}.png"
    ).distinct()

    return PokemonDetail(
        id = id,
        name = name.replaceFirstChar { it.uppercase() },
        imageUrl = primaryImage,
        imageUrls = imageList,
        description = null,
        height = height,
        weight = weight,
        types = typesDomain,
        baseStats = statsMap
    )
}