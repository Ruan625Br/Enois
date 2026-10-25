package dev.jn.enois.domain.util

private val ESCAPABLE = "\\`*_{}[]()#+-.!~|>".toSet()

private val HEADING = Regex("""^(#{1,3})\s+(.*)$""")
private val QUOTE = Regex("""^>\s?(.*)$""")
private val DIVIDER = Regex("""^\s*([-*_])\1{2,}\s*$""")
private val BULLET = Regex("""^(\s*)[-*+]\s+(.*)$""")
private val ORDERED = Regex("""^(\s*)\d+[.)]\s+(.*)$""")
private val FENCE = Regex("""^\s*```""")
private val TASK = Regex("""^\[([ xX])]\s+(.*)$""")
private val TABLE_ROW = Regex("""^\s*\|(.+)\|\s*$""")
private val TABLE_DIVIDER = Regex("""^\s*\|?[\s:|-]+\|[\s:|-]*$""")
private val CELL_START = Regex("""^\s*\|""")
private val CELL_END = Regex("""\|\s*$""")
private val URL_START = Regex("""https?://""", RegexOption.IGNORE_CASE)
private val SCHEME = Regex("""^([a-z][a-z0-9+.-]*):""", RegexOption.IGNORE_CASE)
private val MEDIA_MARKER = Regex("""\{\{media:([A-Za-z0-9_-]+)\}\}""")

private class OpenList(
    val indent: Int,
    val list: RichBlock.ListBlock,
    val siblings: MutableList<RichBlock>,
)

fun parseRichText(content: String): List<RichBlock> {
    val lines = content.replace("\r\n", "\n").split('\n')
    val blocks = mutableListOf<RichBlock>()
    val stack = mutableListOf<OpenList>()
    var paragraph = mutableListOf<String>()
    var openQuote: RichBlock.Quote? = null


    fun flushParagraph() {
        if (paragraph.isEmpty()) return
        val text = paragraph.joinToString("\n")
        paragraph = mutableListOf()
        if (text.isNotBlank()) blocks += RichBlock.Paragraph(parseInline(text.trimEnd('\n')))
    }

    fun listFor(indent: Int, ordered: Boolean): RichBlock.ListBlock {
        while (stack.isNotEmpty() && indent + 2 <= stack.last().indent) {
            stack.removeAt(stack.lastIndex)
        }
        val top = stack.lastOrNull()
        if (top != null && indent >= top.indent + 2) {
            val last = top.list.items.lastOrNull()
            if (last != null) {
                val nested = RichBlock.ListBlock(ordered)
                last.nested += nested
                stack += OpenList(indent, nested, last.nested)
                return nested
            }
        }
        if (top != null && top.list.ordered == ordered) return top.list

        val siblings = top?.siblings ?: blocks
        val created = RichBlock.ListBlock(ordered)
        siblings += created
        stack += OpenList(indent, created, siblings)
        return created
    }

    var i = 0
    while (i < lines.size) {
        val line = lines[i]
        if (!QUOTE.matches(line)) openQuote = null

        if (FENCE.containsMatchIn(line)) {
            flushParagraph(); stack.clear()
            val body = mutableListOf<String>()
            i++
            while (i < lines.size && !FENCE.containsMatchIn(lines[i])) {
                body += lines[i]; i++
            }
            blocks += RichBlock.CodeBlock(body.joinToString("\n"))
            i++                                         // cerca de fechamento (ou fim do texto)
            continue
        }

        // Tabela: a linha de pipes só vira tabela se a seguinte for o separador
        val head = TABLE_ROW.matchEntire(line)
        val next = lines.getOrNull(i + 1)
        if (head != null && next != null && next.contains('-') && TABLE_DIVIDER.matches(next)) {
            flushParagraph(); stack.clear()
            val align = splitCells(next).map(::alignmentOf)
            val header = rowCells(head.groupValues[1], align)
            val rows = mutableListOf<List<RichBlock.TableCell>>()
            i += 2
            while (i < lines.size) {
                val body = TABLE_ROW.matchEntire(lines[i]) ?: break
                rows += rowCells(body.groupValues[1], align)
                i++
            }
            blocks += RichBlock.Table(header, rows)
            continue
        }

        val heading = HEADING.matchEntire(line)
        if (heading != null) {
            flushParagraph(); stack.clear()
            blocks += RichBlock.Heading(
                heading.groupValues[1].length,
                parseInline(heading.groupValues[2])
            )
            i++; continue
        }

        val quote = QUOTE.matchEntire(line)
        if (quote != null) {
            flushParagraph(); stack.clear()
            val inlines = parseInline(quote.groupValues[1])
            val open = openQuote
            if (open != null) {
                open.lines += inlines
            } else {
                val created = RichBlock.Quote(mutableListOf(inlines))
                blocks += created
                openQuote = created
            }
            i++; continue
        }

        if (DIVIDER.matches(line)) {
            flushParagraph(); stack.clear()
            blocks += RichBlock.Divider
            i++; continue
        }

        val bullet = BULLET.matchEntire(line)
        val ordered = ORDERED.matchEntire(line)
        val match = bullet ?: ordered
        if (match != null) {
            flushParagraph()
            val list = listFor(match.groupValues[1].length, ordered != null)
            val raw = match.groupValues[2].trimEnd()
            val task = TASK.matchEntire(raw)
            list.items += RichBlock.ListItem(
                inlines = parseInline(task?.groupValues?.get(2) ?: raw),
                checked = task?.let { it.groupValues[1].lowercase() == "x" },
            )
            i++; continue
        }

        stack.clear()
        paragraph += line
        i++
    }

    flushParagraph()
    return blocks
}

private fun splitCells(row: String): List<String> =
    row.replace(CELL_START, "").replace(CELL_END, "").split("|")

private fun rowCells(row: String, align: List<CellAlign?>): List<RichBlock.TableCell> =
    splitCells(row).mapIndexed { index, cell ->
        RichBlock.TableCell(align.getOrNull(index), parseInline(cell.trim()))
    }

private fun alignmentOf(cell: String): CellAlign? {
    val t = cell.trim()
    return when {
        t.startsWith(":") && t.endsWith(":") -> CellAlign.CENTER
        t.endsWith(":") -> CellAlign.END
        else -> null
    }
}


private fun String.at(i: Int): Char? = getOrNull(i)
private fun Char?.isWord() = this != null && (isLetterOrDigit() || this == '_')

private fun isEscapedAt(text: String, pos: Int): Boolean {
    var count = 0
    var i = pos - 1
    while (i >= 0 && text[i] == '\\') {
        count++; i--
    }
    return count % 2 == 1
}

private enum class Wrap { BOLD, ITALIC, STRIKE, SPOILER }
private class Wrapped(val kind: Wrap, val content: String, val end: Int)
private class Destination(val label: String, val url: String, val end: Int)

fun parseInline(text: String): List<RichInline> {
    val nodes = mutableListOf<RichInline>()
    val plain = StringBuilder()
    var index = 0

    fun flush() {
        if (plain.isNotEmpty()) {
            nodes += RichInline.Text(plain.toString()); plain.clear()
        }
    }

    fun take(node: RichInline, end: Int) {
        flush(); nodes += node; index = end
    }

    while (index < text.length) {
        val char = text[index]

        if (char == '\\' && index + 1 < text.length && text[index + 1] in ESCAPABLE) {
            plain.append(text[index + 1]); index += 2
            continue
        }

        if (char == '`') {
            val close = text.indexOf('`', index + 1)
            if (close > index + 1) {
                take(RichInline.Code(text.substring(index + 1, close)), close + 1); continue
            }
        }

        if (char == '!' && text.at(index + 1) == '[') {
            val image = readDestination(text, index + 1)
            if (image != null) {
                take(RichInline.Image(image.url, image.label), image.end); continue
            }
        }

        if (char == '[') {
            val link = readDestination(text, index)
            if (link != null) {
                take(RichInline.Link(link.url, parseInline(link.label)), link.end); continue
            }
        }

        if (char == '*' || char == '_' || char == '~' || char == '|') {
            val wrapped = readWrapped(text, index, "**", Wrap.BOLD)
                ?: readWrapped(
                    text,
                    index,
                    "__",
                    Wrap.BOLD,
                    leftBoundary = true,
                    rightBoundary = true
                )
                ?: readWrapped(text, index, "*", Wrap.ITALIC, leftBoundary = true)
                ?: readWrapped(
                    text,
                    index,
                    "_",
                    Wrap.ITALIC,
                    leftBoundary = true,
                    rightBoundary = true
                )
                ?: readWrapped(text, index, "~~", Wrap.STRIKE)
                ?: readWrapped(text, index, "||", Wrap.SPOILER)
            if (wrapped != null) {
                val node = when (wrapped.kind) {
                    Wrap.BOLD -> RichInline.Bold(parseInline(wrapped.content))
                    Wrap.ITALIC -> RichInline.Italic(parseInline(wrapped.content))
                    Wrap.STRIKE -> RichInline.Strike(parseInline(wrapped.content))
                    Wrap.SPOILER -> RichInline.Spoiler(wrapped.content)   // texto cru, nunca vira link antes da hora
                }
                take(node, wrapped.end)
                continue
            }
        }

        if (char == '@' && !text.at(index - 1).isWord() && text.at(index + 1).isWord()) {
            var end = index + 1
            while (end < text.length &&
                (text[end].isWord() || (text[end] == '-' && text.at(end + 1).isWord()))
            ) end++
            take(RichInline.Mention(text.substring(index + 1, end)), end)
            continue
        }

        if ((char == 'h' || char == 'H') && URL_START.matchesAt(text, index)) {
            val end = readBareUrl(text, index)
            val url = text.substring(index, end)
            take(RichInline.Link(url, listOf(RichInline.Text(url))), end)
            continue
        }

        plain.append(char)
        index++
    }

    flush()
    return nodes
}

private fun readDestination(text: String, start: Int): Destination? {
    if (text.at(start) != '[') return null

    var depth = 0
    var close = -1
    for (i in start until text.length) {
        val c = text[i]
        if (c == '\n') return null
        if (c == '[' && !isEscapedAt(text, i)) depth++
        if (c == ']' && !isEscapedAt(text, i)) {
            depth--
            if (depth == 0) {
                close = i; break
            }
        }
    }
    if (close == -1 || text.at(close + 1) != '(') return null

    val end = text.indexOf(')', close + 2)
    if (end == -1) return null

    val url = text.substring(close + 2, end).trim()
    if (url.isEmpty() || url.any { it.isWhitespace() } || !isSafeMarkdownUrl(url)) return null
    return Destination(text.substring(start + 1, close), url, end + 1)
}

/** `[x](javascript:...)` fica como texto (E-07): só http(s), caminho e âncora. */
private fun isSafeMarkdownUrl(url: String): Boolean {
    if (url.startsWith("/") || url.startsWith("#")) return true
    val scheme = SCHEME.find(url)?.groupValues?.get(1)?.lowercase()
    return scheme == null || scheme == "http" || scheme == "https"
}

private fun readWrapped(
    text: String,
    start: Int,
    delimiter: String,
    kind: Wrap,
    leftBoundary: Boolean = false,
    rightBoundary: Boolean = false,
): Wrapped? {
    if (!text.startsWith(delimiter, start)) return null
    if (leftBoundary && text.at(start - 1).isWord()) return null

    val from = start + delimiter.length
    val first = text.at(from) ?: return null
    if (first.isWhitespace()) return null

    var search = from
    while (search < text.length) {
        val close = text.indexOf(delimiter, search)
        if (close == -1) return null
        if (isEscapedAt(text, close)) {
            search = close + 1; continue
        }

        val content = text.substring(from, close)
        val after = text.at(close + delimiter.length)
        if (content.isNotEmpty() && !content.last()
                .isWhitespace() && !(rightBoundary && after.isWord())
        ) {
            return Wrapped(kind, content, close + delimiter.length)
        }
        search = close + 1
    }
    return null
}

private fun readBareUrl(text: String, start: Int): Int {
    var end = start
    while (end < text.length && !text[end].isWhitespace()) end++
    while (end > start && text[end - 1] in ".,;:!?") end--
    if (text[end - 1] == ')' && !text.substring(start, end).contains('(')) end--
    return end
}

fun splitByInlineMedia(content: String): List<InlineChunk> {
    val chunks = mutableListOf<InlineChunk>()
    var last = 0
    for (m in MEDIA_MARKER.findAll(content)) {
        if (m.range.first > last) chunks += InlineChunk.Text(content.substring(last, m.range.first))
        chunks += InlineChunk.Media(m.groupValues[1])
        last = m.range.last + 1
    }
    if (last < content.length) chunks += InlineChunk.Text(content.substring(last))
    return chunks
}