package com.chelo.pokemon.feature.home.data.source

import android.content.Context
import com.chelo.pokemon.core.store.EncryptedPreferences
import com.chelo.pokemon.feature.home.domain.entities.PokemonDetail
import com.chelo.pokemon.feature.home.domain.entities.PokemonPage
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PokemonLocalDataSource @Inject constructor(
    private val gson: Gson,
    @ApplicationContext private val context: Context,
) {

    private val sharedPrefs = EncryptedPreferences(context, "pokemon_secure_key")

    private fun pageKey(limit: Int, offset: Int) =
        "pokemon_page_${limit}_$offset"

    private fun detailKey(name: String) =
        "pokemon_detail_${name.lowercase()}"

    private val favoritesKey = "pokemon_favorites"
    private val recentSearchesKey = "recent_searches"


    fun savePokemonPage(limit: Int, offset: Int, page: PokemonPage) {
        val json = gson.toJson(page)

        sharedPrefs.saveEncrypted(pageKey(limit, offset), json)
    }

    fun getPokemonPage(limit: Int, offset: Int): PokemonPage? {

        val json: String? = sharedPrefs.getDecrypted(pageKey(limit, offset))

        if (json.isNullOrEmpty()) return null

        return try {
            gson.fromJson(json, PokemonPage::class.java)
        } catch (e: Exception) {
            null
        }
    }


    fun savePokemonDetail(detail: PokemonDetail) {
        val json = gson.toJson(detail)

        sharedPrefs.saveEncrypted(detailKey(detail.name), json)
    }

    fun getPokemonDetail(name: String): PokemonDetail? {

        val json: String? = sharedPrefs.getDecrypted(detailKey(name))

        if (json.isNullOrEmpty()) return null

        return try {
            gson.fromJson(json, PokemonDetail::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun getFavorites(): Set<String> {
        val json = sharedPrefs.getDecrypted(favoritesKey) ?: return emptySet()
        return try {
            gson.fromJson(json, Array<String>::class.java).toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    fun isFavorite(name: String): Boolean = getFavorites().contains(name.lowercase())

    fun addFavorite(name: String) {
        val set = getFavorites().toMutableSet()
        set.add(name.lowercase())
        sharedPrefs.saveEncrypted(favoritesKey, gson.toJson(set.toTypedArray()))
    }

    fun removeFavorite(name: String) {
        val set = getFavorites().toMutableSet()
        set.remove(name.lowercase())
        sharedPrefs.saveEncrypted(favoritesKey, gson.toJson(set.toTypedArray()))
    }

    fun getRecentSearches(): List<String> {
        val json = sharedPrefs.getDecrypted(recentSearchesKey) ?: return emptyList()
        return try {
            gson.fromJson(json, Array<String>::class.java).toList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveSearchQuery(query: String, maxItems: Int = 10) {
        val norm = query.trim()
        if (norm.isEmpty()) return
        val list = getRecentSearches().toMutableList()
        list.removeAll { it.equals(norm, ignoreCase = true) }
        list.add(0, norm)
        while (list.size > maxItems) list.removeLastOrNull()
        sharedPrefs.saveEncrypted(recentSearchesKey, gson.toJson(list.toTypedArray()))
    }
}