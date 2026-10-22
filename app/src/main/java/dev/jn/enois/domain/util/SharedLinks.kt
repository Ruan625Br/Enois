package dev.jn.enois.domain.util

private val SHARED_URL_REGEX = Regex("""https?://\S+""", RegexOption.IGNORE_CASE)


fun gifMediaUrl(url: String): String? {
    if (
        Regex("""^https?://(media|i)\.giphy\.com/""").containsMatchIn(url) ||
        Regex("""^https?://media\d*\.tenor\.com/""").containsMatchIn(url) ||
        Regex("""\.gif(\?|$)""", RegexOption.IGNORE_CASE).containsMatchIn(url)
    ) {
        return url
    }

    val giphyRegex = Regex(
        """giphy\.com/(?:gifs/)?[^/?#]*-([a-zA-Z0-9]+)/?$"""
    )

    val giphyId = giphyRegex.find(url)?.groupValues?.getOrNull(1)

    return giphyId?.let { id ->
        "https://media.giphy.com/media/$id/giphy.gif"
    }
}

enum class DirectMediaKind {
    IMAGE,
    VIDEO,
    AUDIO,
}

fun directMediaKind(url: String): DirectMediaKind? {
    val path = url
        .substringBefore('?')
        .substringBefore('#')
        .lowercase()

    return when {
        Regex("""\.(jpe?g|png|webp|avif|gif)$""").containsMatchIn(path) ->
            DirectMediaKind.IMAGE

        Regex("""\.(mp4|webm|mov|m4v)$""").containsMatchIn(path) ->
            DirectMediaKind.VIDEO

        Regex("""\.(mp3|ogg|oga|wav|m4a|aac)$""").containsMatchIn(path) ->
            DirectMediaKind.AUDIO

        else -> null
    }
}



fun rendersInline(url: String): Boolean {
    //if (isOwnMediaUrl(url)) return true

    if (gifMediaUrl(url) != null) return true

    return directMediaKind(url) != null
}


fun trimTrailingPunctuation(url: String): String {
    return url.replace(
        Regex("""[.,;:!?»”"')\]}]+$"""),
        "",
    )
}


fun hideExternalPageUrls(content: String): String {
    val cleaned = SHARED_URL_REGEX.replace(content) { match ->
        val originalUrl = match.value
        val url = trimTrailingPunctuation(originalUrl)

        if (rendersInline(url)) {
            url + originalUrl.removePrefix(url)
        } else {
            ""
        }
    }

    return cleaned
        .split('\n')
        .joinToString("\n") { line ->
            line.trimEnd(' ', '\t')
        }
        .replace(Regex("""\n{3,}"""), "\n\n")
        .trim()
}

