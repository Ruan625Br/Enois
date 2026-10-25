package dev.jn.enois.presentation.components.richtext

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.em
import dev.jn.enois.domain.util.RichInline

internal class RichCtx(
    val webOrigin: String,
    val mediaSpoiler: Boolean,
    val onMention: (String) -> Unit,
)

private class InlineStyles(
    val link: TextLinkStyles,
    val mention: TextLinkStyles,
    val code: SpanStyle,
    val spoilerHidden: TextLinkStyles,
    val spoilerShown: SpanStyle,
    val revealed: Set<Int>,
    val onReveal: (Int) -> Unit,
    val ctx: RichCtx,
)

@Composable
internal fun InlineText(
    inlines: List<RichInline>,
    style: TextStyle,
    ctx: RichCtx,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textAlign: TextAlign = TextAlign.Unspecified,
) {
    val scheme = MaterialTheme.colorScheme
    var revealed by remember { mutableStateOf(emptySet<Int>()) }

    val text = remember(inlines, revealed, scheme, ctx.webOrigin) {
        val styles = InlineStyles(
            link = TextLinkStyles(
                SpanStyle(
                    color = scheme.primary,
                    textDecoration = TextDecoration.Underline
                )
            ),
            mention = TextLinkStyles(
                SpanStyle(
                    color = scheme.primary,
                    fontWeight = FontWeight.Medium
                )
            ),
            code = SpanStyle(
                fontFamily = FontFamily.Monospace,
                background = scheme.surfaceContainerHighest,
                fontSize = 0.9.em,
            ),
            spoilerHidden = TextLinkStyles(
                SpanStyle(color = scheme.onSurfaceVariant, background = scheme.onSurfaceVariant)
            ),
            spoilerShown = SpanStyle(background = scheme.surfaceContainerHighest),
            revealed = revealed,
            onReveal = { revealed = revealed + it },
            ctx = ctx,
        )
        buildAnnotatedString { appendInlines(inlines, styles, intArrayOf(0)) }
    }

    Text(
        text = text,
        style = style,
        color = color,
        textAlign = textAlign,
        modifier = modifier
    )
}

private fun AnnotatedString.Builder.appendInlines(
    nodes: List<RichInline>,
    s: InlineStyles,
    spoilerCounter: IntArray,
) {
    nodes.forEach { node ->
        when (node) {
            is RichInline.Text -> append(node.value)
            is RichInline.Bold -> withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                appendInlines(node.children, s, spoilerCounter)
            }

            is RichInline.Italic -> withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                appendInlines(node.children, s, spoilerCounter)
            }

            is RichInline.Strike -> withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                appendInlines(node.children, s, spoilerCounter)
            }

            is RichInline.Code -> withStyle(s.code) { append(node.value) }

            is RichInline.Mention -> withLink(
                LinkAnnotation.Clickable("mention:${node.username}", s.mention) {
                    s.ctx.onMention(
                        node.username
                    )
                }
            ) { append("@${node.username}") }

            is RichInline.Spoiler -> {
                val index = spoilerCounter[0]++
                if (index in s.revealed) {
                    withStyle(s.spoilerShown) { append(node.value) }
                } else {
                    withLink(
                        LinkAnnotation.Clickable(
                            "spoiler:$index",
                            s.spoilerHidden
                        ) { s.onReveal(index) }) {
                        append(node.value)
                    }
                }
            }

            is RichInline.Link -> {
                val href = safeHref(node.href, s.ctx.webOrigin)
                if (href == null) appendInlines(node.children, s, spoilerCounter)
                else withLink(LinkAnnotation.Url(href, s.link)) {
                    appendInlines(
                        node.children,
                        s,
                        spoilerCounter
                    )
                }
            }

            is RichInline.Image -> {
                val href = safeHref(node.href, s.ctx.webOrigin)
                val label = node.alt.ifBlank { node.href }
                if (href == null) append(label)
                else withLink(LinkAnnotation.Url(href, s.link)) { append(label) }
            }
        }
    }
}

internal fun safeHref(href: String?, webOrigin: String): String? {
    if (href.isNullOrBlank()) return null
    if (href.startsWith("/")) return webOrigin.trimEnd('/') + href
    val scheme = Regex("""^([a-z][a-z0-9+.-]*):""", RegexOption.IGNORE_CASE)
        .find(href)?.groupValues?.get(1)?.lowercase()
    return if (scheme == "http" || scheme == "https") href else null
}