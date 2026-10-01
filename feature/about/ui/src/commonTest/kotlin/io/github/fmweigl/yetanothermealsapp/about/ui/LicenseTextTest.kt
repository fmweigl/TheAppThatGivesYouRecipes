package io.github.fmweigl.yetanothermealsapp.about.ui

import kotlin.test.Test
import kotlin.test.assertEquals

class LicenseTextTest {

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
                "Apache License Version 2.0, January 2004",
                "1. Definitions.",
                "\"License\" shall mean the terms and conditions for use, reproduction, and distribution.",
            ),
            toParagraphs(text),
        )
    }

    @Test
    fun treatsWhitespaceOnlyLinesAsParagraphBreaks() {
        assertEquals(listOf("a b", "c"), toParagraphs("a\nb\n   \nc\n"))
    }

    @Test
    fun returnsNoParagraphsForBlankText() {
        assertEquals(emptyList(), toParagraphs(" \n\n "))
    }
}
