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



    fun savePokemonPage(limit: Int, offset: Int, page: PokemonPage) {
        val json = gson.toJson(page)


         sharedPrefs.saveEncrypted(pageKey(limit, offset), json)

    }

    fun getPokemonPage(limit: Int, offset: Int): PokemonPage? {

        // val json = sharedPrefs.getString(pageKey(limit, offset), null)
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

        // val json = sharedPrefs.getString(detailKey(name), null)
        val json: String? = sharedPrefs.getDecrypted(detailKey(name))

        if (json.isNullOrEmpty()) return null

        return try {
            gson.fromJson(json, PokemonDetail::class.java)
        } catch (e: Exception) {
            null
        }
    }
}