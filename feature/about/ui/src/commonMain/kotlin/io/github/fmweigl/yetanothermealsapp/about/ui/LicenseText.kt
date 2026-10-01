package io.github.fmweigl.yetanothermealsapp.about.ui

/**
 * Splits a plain text license into paragraphs at blank lines and joins each paragraph's lines, so
 * the text wraps to the screen width instead of keeping the file's fixed line breaks and indentation.
 */
internal fun toParagraphs(text: String): List<String> =
    text.split(Regex("""\n\s*\n"""))
        .map { paragraph -> paragraph.lines().map(String::trim).filter(String::isNotEmpty).joinToString(" ") }
        .filter(String::isNotEmpty)
