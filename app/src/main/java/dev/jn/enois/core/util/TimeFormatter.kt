package dev.jn.enois.core.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatTime(iso: String?): String {
    val millis = parseIsoMillis(iso) ?: return ""
    val diffMinutes = (System.currentTimeMillis() - millis) / 60_000

    return when {
        diffMinutes < 1 -> "agora"
        diffMinutes < 60 -> "há $diffMinutes min"
        diffMinutes < 1440 -> "há ${diffMinutes / 60} h"
        diffMinutes < 10080 -> {
            val days = diffMinutes / 1440
            "há $days ${if (days == 1L) "dia" else "dias"}"
        }

        else -> DateTimeFormatter
            .ofPattern("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"))
            .withZone(ZoneId.of("America/Sao_Paulo"))
            .format(Instant.ofEpochMilli(millis))
    }
}