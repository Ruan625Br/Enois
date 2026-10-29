package dev.jn.enois.data.local.emote

import android.content.Context
import androidx.core.content.edit
import kotlinx.serialization.json.Json

class EmoteIndexStore(context: Context) {
    private val prefs = context.getSharedPreferences("emotes", Context.MODE_PRIVATE)

    fun read(): Map<String, String>? = prefs.getString(KEY, null)?.let {
        runCatching { Json.decodeFromString<Map<String, String>>(it) }.getOrNull()
    }

    fun write(entries: Map<String, String>) {
        if (entries.isEmpty()) return
        prefs.edit { putString(KEY, Json.encodeToString(entries)) }
    }

    private companion object { const val KEY = "index_v1" }
}