package dev.jn.enois.data.local.session

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import io.ktor.http.Cookie
import io.ktor.http.CookieEncoding
import io.ktor.util.date.GMTDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class SecureCookieStore(
    context: Context,
) {

    private val preferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun read(): List<Cookie> = withContext(Dispatchers.IO) {
        val encrypted = preferences.getString(KEY_COOKIES, null)
            ?: return@withContext emptyList()

        try {
            val combined = Base64.decode(encrypted, Base64.NO_WRAP)
            require(combined.size > IV_SIZE) { "Payload inválido." }

            val iv = combined.copyOfRange(0, IV_SIZE)
            val ciphertext = combined.copyOfRange(IV_SIZE, combined.size)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(
                Cipher.DECRYPT_MODE,
                getOrCreateKey(),
                GCMParameterSpec(TAG_SIZE_BITS, iv)
            )

            val text = cipher.doFinal(ciphertext).toString(Charsets.UTF_8)

            json.decodeFromString<List<StoredCookie>>(text).map { it.toCookie() }
        } catch (e: Exception) {
            runCatching { clearStoredValue() }
            emptyList()
        }
    }

    suspend fun write(cookies: List<Cookie>) = withContext(Dispatchers.IO) {
        val text = json.encodeToString(cookies.map { it.toStored() })

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())

        val ciphertext = cipher.doFinal(text.toByteArray(Charsets.UTF_8))
        val combined = cipher.iv + ciphertext

        val encrypted = Base64.encodeToString(combined, Base64.NO_WRAP)

        check(
            preferences.edit()
                .putString(KEY_COOKIES, encrypted)
                .commit()
        ) {
            "Não foi possível persistir os cookies."
        }
    }

    suspend fun clear() = withContext(Dispatchers.IO) {
        clearStoredValue()
    }


    private fun clearStoredValue() {
        check(
            preferences.edit()
                .remove(KEY_COOKIES)
                .commit()
        ) {
            "Não foi possível limpar os cookies."
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
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )

        return keyGenerator.generateKey()
    }

    @Serializable
    private data class StoredCookie(
        val name: String,
        val value: String,
        val encoding: String = CookieEncoding.URI_ENCODING.name,
        val maxAge: Int? = 0,
        val expires: Long? = null,
        val domain: String? = null,
        val path: String? = null,
        val secure: Boolean = false,
        val httpOnly: Boolean = false,
        val extensions: Map<String, String?> = emptyMap(),
    ) {
        fun toCookie() = Cookie(
            name = name,
            value = value,
            encoding = CookieEncoding.valueOf(encoding),
            maxAge = maxAge,
            expires = expires?.let { GMTDate(it) },
            domain = domain,
            path = path,
            secure = secure,
            httpOnly = httpOnly,
            extensions = extensions,
        )
    }

    private fun Cookie.toStored() = StoredCookie(
        name = name,
        value = value,
        encoding = encoding.name,
        maxAge = maxAge,
        expires = expires?.timestamp,
        domain = domain,
        path = path,
        secure = secure,
        httpOnly = httpOnly,
        extensions = extensions,
    )

    private companion object {
        const val PREFS_NAME = "enois_secure_cookies"
        const val KEY_COOKIES = "encrypted_cookies"
        const val KEY_ALIAS = "enois_cookies_key"
        const val KEYSTORE_PROVIDER = "AndroidKeyStore"

        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val IV_SIZE = 12
        const val TAG_SIZE_BITS = 128
    }
}