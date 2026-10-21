package dev.jn.enois.core.util

import android.annotation.SuppressLint
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@SuppressLint("NewApi")
fun formatTime(iso: String?): String {
    if (iso.isNullOrBlank()) return ""

    val date = runCatching {
        runCatching { Instant.parse(iso) }
            .getOrElse { OffsetDateTime.parse(iso).toInstant() }
    }.getOrNull() ?: return ""

    val diffMinutes = Duration.between(date, Instant.now()).toMinutes()

    return when {
        diffMinutes < 1 -> "Agora"
        diffMinutes < 60 -> "$diffMinutes min atrás"
        diffMinutes < 1440 -> "${diffMinutes / 60} h atrás"
        diffMinutes < 10080 -> "${diffMinutes / 1440} d atrás"
        else -> DateTimeFormatter
            .ofPattern("dd/MM/yyyy", Locale("pt", "BR"))
            .withZone(java.time.ZoneId.of("America/Sao_Paulo"))
            .format(date)
    }
}