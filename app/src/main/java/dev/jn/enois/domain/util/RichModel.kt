package dev.jn.enois.domain.util

sealed interface RichInline {
    data class Text(val value: String) : RichInline
    data class Bold(val children: List<RichInline>) : RichInline
    data class Italic(val children: List<RichInline>) : RichInline
    data class Strike(val children: List<RichInline>) : RichInline
    data class Code(val value: String) : RichInline
    data class Mention(val username: String) : RichInline
    data class Spoiler(val value: String) : RichInline
    data class Link(val href: String, val children: List<RichInline>) : RichInline
    data class Image(val href: String, val alt: String) : RichInline
}

enum class CellAlign { CENTER, END }

data class ListItem(val inlines: List<RichInline>, val checked: Boolean?, val nested: List<RichBlock>)

sealed interface RichBlock {
    class Paragraph(val inlines: List<RichInline>) : RichBlock
    class Heading(val level: Int, val inlines: List<RichInline>) : RichBlock
    class Quote(val lines: MutableList<List<RichInline>>) : RichBlock
    class CodeBlock(val code: String) : RichBlock
    data object Divider : RichBlock
    class ListBlock(val ordered: Boolean) : RichBlock {
        val items = mutableListOf<ListItem>()
    }
    class Table(val header: List<TableCell>, val rows: List<List<TableCell>>) : RichBlock

    class ListItem(val inlines: List<RichInline>, val checked: Boolean?) {
        val nested = mutableListOf<RichBlock>()
    }
    class TableCell(val align: CellAlign?, val inlines: List<RichInline>)
}

sealed interface InlineChunk {
    data class Text(val text: String) : InlineChunk
    data class Media(val mediaId: String) : InlineChunk
}