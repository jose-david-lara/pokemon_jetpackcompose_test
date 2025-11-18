package com.chelo.pokemon.core.security

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object EncryptionUtils {

    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val IV_SIZE = 12
    private const val TAG_LENGTH = 128

    fun encrypt(input: String, key: SecretKey): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        val iv = ByteArray(IV_SIZE).apply { SecureRandom().nextBytes(this) }
        val spec = GCMParameterSpec(TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, key, spec)
        val encrypted = cipher.doFinal(input.toByteArray())
        val combined = iv + encrypted

        return Base64.encodeToString(combined, Base64.NO_WRAP)

    }

    fun decrypt(input: String, key: SecretKey): String {
        val decoded = java.util.Base64.getDecoder().decode(input)

        val iv = decoded.copyOfRange(0, IV_SIZE)
        val encrypted = decoded.copyOfRange(IV_SIZE, decoded.size)
        val cipher = Cipher.getInstance(ALGORITHM)
        val spec = GCMParameterSpec(TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)

        return String(cipher.doFinal(encrypted))
    }

    fun generateKey(key: String): SecretKeySpec {
        val keyBytes = key.toByteArray()
        val sha256 = MessageDigest.getInstance("SHA-256")
        val hashBytes = sha256.digest(keyBytes)
        return SecretKeySpec(hashBytes, "AES")
    }

}
