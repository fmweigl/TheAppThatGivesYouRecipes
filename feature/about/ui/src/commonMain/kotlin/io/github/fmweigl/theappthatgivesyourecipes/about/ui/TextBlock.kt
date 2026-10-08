package io.github.fmweigl.theappthatgivesyourecipes.about.ui

/** A paragraph of a text document; [headingLevel] is 0 for body text, else the number of `#`s. */
internal data class TextBlock(val text: String, val headingLevel: Int = 0)

private val heading = Regex("""^(#+)\s+(.*)$""")

/**
 * Splits a plain text or simple Markdown document (a license, the privacy policy) into blocks:
 * `#` lines become headings, and the lines of each paragraph are joined, so the text wraps to the
 * screen width instead of keeping the file's fixed line breaks and indentation.
 */
internal fun toTextBlocks(text: String): List<TextBlock> {
    val blocks = mutableListOf<TextBlock>()
    val paragraph = mutableListOf<String>()
    fun endParagraph() {
        if (paragraph.isNotEmpty()) blocks += TextBlock(paragraph.joinToString(" "))
        paragraph.clear()
    }
    for (line in text.lines().map(String::trim)) {
        val headingMatch = heading.matchEntire(line)
        when {
            line.isEmpty() -> endParagraph()
            headingMatch != null -> {
                endParagraph()
                val (hashes, title) = headingMatch.destructured
                blocks += TextBlock(title, headingLevel = hashes.length)
            }
            else -> paragraph += line
        }
    }
    endParagraph()
    return blocks
}
