package com.ramo.shared

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Play: ImageVector
    get() {
        if (_Play != null) return _Play!!

        _Play = ImageVector.Builder(
            name = "Play",
            defaultWidth = 16.dp,
            defaultHeight = 16.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(10.804f, 8f)
                lineTo(5f, 4.633f)
                verticalLineToRelative(6.734f)
                close()
                moveToRelative(0.792f, -0.696f)
                arcToRelative(0.802f, 0.802f, 0f, false, true, 0f, 1.392f)
                lineToRelative(-6.363f, 3.692f)
                curveTo(4.713f, 12.69f, 4f, 12.345f, 4f, 11.692f)
                verticalLineTo(4.308f)
                curveToRelative(0f, -0.653f, 0.713f, -0.998f, 1.233f, -0.696f)
                close()
            }
        }.build()

        return _Play!!
    }

private var _Play: ImageVector? = null


val Pause: ImageVector
    get() {
        if (_Pause != null) return _Pause!!

        _Pause = ImageVector.Builder(
            name = "Pause",
            defaultWidth = 16.dp,
            defaultHeight = 16.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(6f, 3.5f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0.5f, 0.5f)
                verticalLineToRelative(8f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, -1f, 0f)
                verticalLineTo(4f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0.5f, -0.5f)
                moveToRelative(4f, 0f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0.5f, 0.5f)
                verticalLineToRelative(8f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, -1f, 0f)
                verticalLineTo(4f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0.5f, -0.5f)
            }
        }.build()

        return _Pause!!
    }

private var _Pause: ImageVector? = null

