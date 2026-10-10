package io.github.fmweigl.theappthatgivesyourecipes.core.designsystem.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private const val ICON_SIZE = 24f
private const val PIP_RADIUS = 1.5f
private const val PIP_DIAMETER = PIP_RADIUS * 2

/**
 * A die with five pips, cut out of the square (even-odd fill), in Material's filled style.
 * Used for the Random tab.
 */
val DieIcon: ImageVector
    get() = _dieIcon ?: ImageVector.Builder(
        name = "Die",
        defaultWidth = ICON_SIZE.dp,
        defaultHeight = ICON_SIZE.dp,
        viewportWidth = ICON_SIZE,
        viewportHeight = ICON_SIZE,
    ).apply {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.EvenOdd) {
            moveTo(6f, 3f)
            horizontalLineTo(18f)
            arcTo(3f, 3f, 0f, false, true, 21f, 6f)
            verticalLineTo(18f)
            arcTo(3f, 3f, 0f, false, true, 18f, 21f)
            horizontalLineTo(6f)
            arcTo(3f, 3f, 0f, false, true, 3f, 18f)
            verticalLineTo(6f)
            arcTo(3f, 3f, 0f, false, true, 6f, 3f)
            close()
            pip(8f, 8f)
            pip(16f, 8f)
            pip(12f, 12f)
            pip(8f, 16f)
            pip(16f, 16f)
        }
    }.build().also { _dieIcon = it }

private var _dieIcon: ImageVector? = null

private fun PathBuilder.pip(centerX: Float, centerY: Float) {
    moveTo(centerX - PIP_RADIUS, centerY)
    arcToRelative(PIP_RADIUS, PIP_RADIUS, 0f, true, false, PIP_DIAMETER, 0f)
    arcToRelative(PIP_RADIUS, PIP_RADIUS, 0f, true, false, -PIP_DIAMETER, 0f)
    close()
}
