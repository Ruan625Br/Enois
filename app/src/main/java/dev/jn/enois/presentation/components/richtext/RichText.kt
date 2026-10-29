package dev.jn.enois.presentation.components.richtext

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jn.enois.BuildConfig
import dev.jn.enois.domain.util.CellAlign
import dev.jn.enois.domain.util.InlineChunk
import dev.jn.enois.domain.util.RichBlock
import dev.jn.enois.domain.util.parseRichText
import dev.jn.enois.domain.util.splitByInlineMedia

@Composable
fun RichText(
    content: String,
    modifier: Modifier = Modifier,
    webOrigin: String = BuildConfig.WEB_ORIGIN,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    mediaIds: List<String> = emptyList(),
    mediaSpoiler: Boolean = false,
    onMention: (username: String) -> Unit = {},
    inlineMedia: @Composable (mediaId: String) -> Unit = {},
) {
    val emotes = LocalEmotes.current
    val ctx = remember(webOrigin, mediaSpoiler, onMention, emotes) {
        RichCtx(webOrigin, mediaSpoiler, onMention, emotes)
    }

    val parts = remember(content, mediaIds.isNotEmpty()) {
        val chunks = if (mediaIds.isNotEmpty()) splitByInlineMedia(content)
        else listOf(InlineChunk.Text(content))
        chunks.map {
            when (it) {
                is InlineChunk.Text -> it.text to parseRichText(it.text)
                is InlineChunk.Media -> it.mediaId to null
            }
        }
    }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        parts.forEach { (textOrId, blocks) ->
            if (blocks == null) {
                if (textOrId in mediaIds) SpoilerMedia(active = mediaSpoiler) { inlineMedia(textOrId) }
            } else {
                blocks.forEach { RichBlockView(it, style, ctx) }
            }
        }
    }
}

@Composable
fun SpoilerMedia(active: Boolean, content: @Composable () -> Unit) {
    TODO("Not yet implemented SpoilerMedia")
}

@Composable
private fun RichBlockView(
    block: RichBlock,
    style: TextStyle,
    ctx: RichCtx,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    when (block) {
        is RichBlock.Paragraph -> ParagraphView(block.inlines, style, ctx, modifier)

        is RichBlock.Heading -> InlineText(
            inlines = block.inlines,
            style = style.copy(
                fontSize = style.fontSize * if (block.level == 1) 1.3f else 1.15f,
                fontWeight = FontWeight.Medium,
            ),
            ctx = ctx,
            modifier = modifier.padding(top = 4.dp),
        )

        is RichBlock.Quote -> Row(modifier.height(IntrinsicSize.Min)) {
            Box(
                Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(scheme.outlineVariant)
            )
            Spacer(Modifier.width(10.dp))
            Column {
                block.lines.forEach { InlineText(it, style, ctx, color = scheme.onSurfaceVariant) }
            }
        }

        is RichBlock.CodeBlock -> Surface(
            shape = MaterialTheme.shapes.medium,
            color = scheme.surfaceContainerHighest,
            modifier = modifier.fillMaxWidth(),
        ) {
            Text(
                text = block.code,
                style = style.copy(fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(12.dp),
            )
        }

        RichBlock.Divider -> HorizontalDivider(modifier.padding(vertical = 4.dp))

        is RichBlock.ListBlock -> Column(
            modifier,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            block.items.forEachIndexed { index, item ->
                Row {
                    Text(
                        text = when {
                            item.checked != null -> if (item.checked) "☑" else "☐"
                            block.ordered -> "${index + 1}."
                            else -> "•"
                        },
                        style = style,
                        modifier = Modifier.widthIn(min = 22.dp),
                    )
                    Column {
                        InlineText(item.inlines, style, ctx)
                        item.nested.forEach {
                            RichBlockView(
                                it,
                                style,
                                ctx,
                                Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        is RichBlock.Table -> TableView(block, style, ctx, modifier)
    }
}

@Composable
private fun TableView(table: RichBlock.Table, style: TextStyle, ctx: RichCtx, modifier: Modifier) {
    val scheme = MaterialTheme.colorScheme
    val columns = maxOf(table.header.size, table.rows.maxOfOrNull { it.size } ?: 0)

    Column(
        modifier
            .horizontalScroll(rememberScrollState())
            .border(1.dp, scheme.outlineVariant)
    ) {
        (listOf(table.header) + table.rows).forEachIndexed { rowIndex, cells ->
            val isHeader = rowIndex == 0
            Row(Modifier.height(IntrinsicSize.Min)) {
                repeat(columns) { col ->
                    val cell = cells.getOrNull(col)
                    Box(
                        Modifier
                            .width(120.dp)
                            .fillMaxHeight()
                            .background(if (isHeader) scheme.surfaceContainerHigh else Color.Transparent)
                            .border(0.5.dp, scheme.outlineVariant)
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                    ) {
                        if (cell != null) {
                            InlineText(
                                inlines = cell.inlines,
                                style = if (isHeader) style.copy(fontWeight = FontWeight.Medium) else style,
                                ctx = ctx,
                                textAlign = when (cell.align) {
                                    CellAlign.CENTER -> TextAlign.Center
                                    CellAlign.END -> TextAlign.End
                                    null -> TextAlign.Start
                                },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        }
    }
}