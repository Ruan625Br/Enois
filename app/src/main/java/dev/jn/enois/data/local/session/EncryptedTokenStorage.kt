package dev.jn.enois.data.local.session

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class EncryptedTokenStorage(
    context: Context,
) : TokenStorage {

    private val preferences = context.getSharedPreferences(
        "enois_secure_session",
        Context.MODE_PRIVATE
    )

    override suspend fun getTokens(): StoredToken? =
        withContext(Dispatchers.IO) {
            val encrypted = preferences.getString(KEY_TOKENS, null)
                ?: return@withContext null

            try {
                val combined = Base64.decode(encrypted, Base64.NO_WRAP)
                val iv = combined.copyOfRange(0, IV_SIZE)
                val ciphertext = combined.copyOfRange(IV_SIZE, combined.size)

                val cipher = Cipher.getInstance(TRANSFORMATION)

                cipher.init(
                    Cipher.DECRYPT_MODE,
                    getOrCreateKey(),
                    GCMParameterSpec(TAG_SIZE_BITS, iv)
                )

                val json = cipher.doFinal(ciphertext)
                    .toString(Charsets.UTF_8)

                val objectJson = JSONObject(json)

                val accessToken = objectJson.getString("accessToken")
                val refreshToken = objectJson.getString("refreshToken")

                if (accessToken.isBlank() || refreshToken.isBlank()) {
                    return@withContext null
                }

                StoredToken(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                )
            } catch (e: Exception) {
                clearStoredValue()
                null
            }
        }


    override suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
    ) =
        withContext(Dispatchers.IO) {
            require(accessToken.isNotBlank()) {
                "Access token não pode ser vazio."
            }
            require(refreshToken.isNotBlank()) {
                "Refresh token não pode ser vazio."
            }

            val json = JSONObject()
                .put("accessToken", accessToken)
                .put("refreshToken", refreshToken)
                .toString()

            val cipher = Cipher.getInstance(TRANSFORMATION)

            cipher.init(
                Cipher.ENCRYPT_MODE,
                getOrCreateKey()
            )

            val ciphertext = cipher.doFinal(json.toByteArray(Charsets.UTF_8))
            val combined = cipher.iv + ciphertext

            val encrypted = Base64.encodeToString(
                combined,
                Base64.NO_WRAP
            )

            check(
                preferences.edit()
                    .putString(KEY_TOKENS, encrypted)
                    .commit()
            ) {
                "Não foi possível persistir os tokens."
            }

        }

    override suspend fun clear() {
        withContext(Dispatchers.IO) {
            clearStoredValue()
        }
    }

    private fun clearStoredValue() {
        check(
            preferences.edit()
                .remove(KEY_TOKENS)
                .commit()
        ) {
            "Não foi possível limpar os tokens."
        }
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply {
            load(null)
        }

        val existingKey = keyStore.getKey(KEY_ALIAS, null)
        if (existingKey is SecretKey) {
            return existingKey
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            KEYSTORE_PROVIDER
        )

        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or
                        KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(
                    KeyProperties.ENCRYPTION_PADDING_NONE
                )
                .setKeySize(256)
                .build()
        )

        return keyGenerator.generateKey()
    }

    private companion object {
        const val KEY_ALIAS = "enois_session_key"
        const val KEY_TOKENS = "encrypted_tokens"
        const val KEYSTORE_PROVIDER = "AndroidKeyStore"

        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val IV_SIZE = 12
        const val TAG_SIZE_BITS = 128
    }
}