package dev.jn.enois.presentation.components.richtext

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import dev.jn.enois.domain.util.DirectMediaKind
import dev.jn.enois.domain.util.RichInline
import dev.jn.enois.domain.util.directMediaKind
import dev.jn.enois.domain.util.gifMediaUrl


private sealed interface Segment {
    data class Inlines(val nodes: List<RichInline>) : Segment
    data class Media(val href: String, val kind: DirectMediaKind, val alt: String = "") : Segment
}

private fun mediaOf(href: String, alt: String = ""): Segment.Media? = when (directMediaKind(href)) {
    DirectMediaKind.VIDEO -> Segment.Media(href, DirectMediaKind.VIDEO, alt)
    DirectMediaKind.AUDIO -> Segment.Media(href, DirectMediaKind.AUDIO, alt)
    DirectMediaKind.IMAGE -> Segment.Media(gifMediaUrl(href) ?: href, DirectMediaKind.IMAGE, alt)
    null -> gifMediaUrl(href)?.let { Segment.Media(it, DirectMediaKind.IMAGE, alt) }
}

private fun splitSegments(inlines: List<RichInline>, webOrigin: String): List<Segment> {
    val out = mutableListOf<Segment>()
    var current = mutableListOf<RichInline>()
    fun flush() {
        if (current.isNotEmpty()) {
            out += Segment.Inlines(current); current = mutableListOf()
        }
    }

    for (node in inlines) {
        val media = when (node) {
            is RichInline.Link -> mediaOf(node.href)
            is RichInline.Image -> safeHref(node.href, webOrigin)?.let { href ->
                mediaOf(href, node.alt)
                    ?: if (isOwnMedia(href, webOrigin)) Segment.Media(href, DirectMediaKind.IMAGE, node.alt) else null
            }
            else -> null
        }
        if (media != null) {
            flush(); out += media
        } else current += node
    }
    flush()

    return out.mapIndexedNotNull { i, seg ->
        if (seg !is Segment.Inlines) return@mapIndexedNotNull seg
        var nodes = seg.nodes
        if (out.getOrNull(i - 1) is Segment.Media) {
            (nodes.firstOrNull() as? RichInline.Text)?.let {
                nodes = listOf(it.copy(value = it.value.trimStart('\n'))) + nodes.drop(1)
            }
        }
        if (out.getOrNull(i + 1) is Segment.Media) {
            (nodes.lastOrNull() as? RichInline.Text)?.let {
                nodes = nodes.dropLast(1) + it.copy(value = it.value.trimEnd('\n'))
            }
        }
        nodes = nodes.filterNot { it is RichInline.Text && it.value.isEmpty() }
        if (nodes.isEmpty()) null else Segment.Inlines(nodes)
    }
}

private fun isOwnMedia(href: String, webOrigin: String): Boolean {
    val host = href.toUri().host ?: return true
    return host == webOrigin.toUri().host || host == "ik.imagekit.io"
}

@Composable
internal fun ParagraphView(
    inlines: List<RichInline>,
    style: TextStyle,
    ctx: RichCtx,
    modifier: Modifier
) {
    val segments = remember(inlines, ctx.webOrigin) { splitSegments(inlines, ctx.webOrigin) }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        segments.forEach { seg ->
            when (seg) {
                is Segment.Inlines -> InlineText(seg.nodes, style, ctx)
                is Segment.Media -> SpoilerMedia(active = ctx.mediaSpoiler) { MediaView(seg) }
            }
        }
    }
}

@Composable
private fun MediaView(seg: Segment.Media) {
    val uriHandler = LocalUriHandler.current
    when (seg.kind) {
        DirectMediaKind.IMAGE -> AsyncImage(
            model = seg.href,
            contentDescription = seg.alt.ifBlank { null },
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 320.dp)
                .clip(RoundedCornerShape(12.dp)),
        )

        DirectMediaKind.VIDEO, DirectMediaKind.AUDIO -> AssistChip(
            onClick = { uriHandler.openUri(seg.href) },
            label = { Text(if (seg.kind == DirectMediaKind.VIDEO) "▶ Abrir vídeo" else "🎧 Abrir áudio") },
        )
    }
}