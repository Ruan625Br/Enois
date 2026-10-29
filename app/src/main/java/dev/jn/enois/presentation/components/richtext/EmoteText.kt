package dev.jn.enois.presentation.components.richtext

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.text.AnnotatedString

internal const val EMOTE_PREFIX = "emote:"

private val TOKEN = Regex("""\S+""")

private val BOUNDARY = Regex("""^([(\[{'"*_~]*)(.*?)([)\]}>.,;:!?'"*_~]*)$""")


internal fun AnnotatedString.Builder.appendWithEmotes(
    text: String,
    index: Map<String, String>,
    used: MutableSet<String>,
) {
    if (index.isEmpty()) {
        append(text)
        return
    }

    var last = 0
    for (match in TOKEN.findAll(text)) {
        val token = match.value
        var pre = ""
        var core = token
        var post = ""

        if (token !in index) {
            val (p, c, s) = BOUNDARY.matchEntire(token)?.destructured ?: continue
            if (c !in index) continue
            pre = p; core = c; post = s
        }

        append(text.substring(last, match.range.first))
        append(pre)
        used += core
        appendInlineContent(EMOTE_PREFIX + core, core)
        append(post)
        last = match.range.last + 1
    }
    append(text.substring(last))
}