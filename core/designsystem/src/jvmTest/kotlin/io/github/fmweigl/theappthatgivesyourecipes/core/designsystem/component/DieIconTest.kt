package io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.component

import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.vector.VectorPath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DieIconTest {
    @Test
    fun viewportIs24By24() {
        assertEquals(24f, DieIcon.viewportWidth)
        assertEquals(24f, DieIcon.viewportHeight)
        assertEquals(24f, DieIcon.defaultWidth.value)
        assertEquals(24f, DieIcon.defaultHeight.value)
    }

    @Test
    fun pathUsesEvenOddFillSoPipsStayTransparent() {
        val path = DieIcon.root.iterator().asSequence().toList().single()
        assertIs<VectorPath>(path)
        assertEquals(PathFillType.EvenOdd, path.pathFillType)
    }
}
