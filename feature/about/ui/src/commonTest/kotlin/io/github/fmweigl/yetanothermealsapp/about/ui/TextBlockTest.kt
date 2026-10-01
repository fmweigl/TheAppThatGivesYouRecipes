package io.github.fmweigl.yetanothermealsapp.about.ui

import kotlin.test.Test
import kotlin.test.assertEquals

class TextBlockTest {

    @Test
    fun joinsTheLinesOfEachParagraphAndDropsIndentation() {
        val text = """
                                 Apache License
                           Version 2.0, January 2004

   1. Definitions.

      "License" shall mean the terms and conditions for use,
      reproduction, and distribution.
        """.trimIndent()

        assertEquals(
            listOf(
                TextBlock("Apache License Version 2.0, January 2004"),
                TextBlock("1. Definitions."),
                TextBlock("\"License\" shall mean the terms and conditions for use, reproduction, and distribution."),
            ),
            toTextBlocks(text),
        )
    }

    @Test
    fun turnsMarkdownHeadingsIntoHeadingBlocks() {
        val text = """
            # Privacy Policy

            Last updated: today
            ## Data I collect
            None. The app has
            no user accounts.
        """.trimIndent()

        assertEquals(
            listOf(
                TextBlock("Privacy Policy", headingLevel = 1),
                TextBlock("Last updated: today"),
                TextBlock("Data I collect", headingLevel = 2),
                TextBlock("None. The app has no user accounts."),
            ),
            toTextBlocks(text),
        )
    }

    @Test
    fun keepsHashesThatAreNotHeadings() {
        assertEquals(listOf(TextBlock("#hashtag and C#")), toTextBlocks("#hashtag and C#"))
    }

    @Test
    fun treatsWhitespaceOnlyLinesAsParagraphBreaks() {
        assertEquals(listOf(TextBlock("a b"), TextBlock("c")), toTextBlocks("a\nb\n   \nc\n"))
    }

    @Test
    fun returnsNoBlocksForBlankText() {
        assertEquals(emptyList(), toTextBlocks(" \n\n "))
    }
}
