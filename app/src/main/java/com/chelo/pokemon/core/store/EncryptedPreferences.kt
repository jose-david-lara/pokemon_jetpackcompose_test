package com.chelo.pokemon.core.store

import android.content.Context
import android.content.SharedPreferences
import com.chelo.pokemon.core.security.EncryptionUtils
import javax.crypto.SecretKey

class EncryptedPreferences(
    context: Context,
    private val encryptionKey: String
) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("secure_prefs_pokemon", Context.MODE_PRIVATE)

    private val secretKey: SecretKey = EncryptionUtils.generateKey(encryptionKey)

    fun saveEncrypted(key: String, value: String) {
        val encrypted = EncryptionUtils.encrypt(value, secretKey)
        prefs.edit().putString(key, encrypted).apply()
    }

    fun getDecrypted(key: String): String? {
        val encrypted = prefs.getString(key, null) ?: return null
        return try {
            EncryptionUtils.decrypt(encrypted, secretKey)
        } catch (e: Exception) {
            null
        }
    }

    fun getExist(key: String): Boolean {
        return prefs.contains(key)
    }

    fun clear(key: String) {
        prefs.edit().remove(key).apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
