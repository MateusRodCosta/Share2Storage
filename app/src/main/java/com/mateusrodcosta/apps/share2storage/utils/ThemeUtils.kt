/*
 *     Copyright (C) 2022 Mateus Rodrigues Costa
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>
 */

package com.mateusrodcosta.apps.share2storage.utils

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val Blue500 = Color(0xFF2196F3)
val Blue700 = Color(0xFF1976D2)
val accent = Color(0xFF21F3E9)
val accentVariant = Color(0xFF001FE5)

@Immutable
data class ExtendedColors(
    val primary: Color,
    val primaryVariant: Color,
    val secondary: Color,
    val secondaryVariant: Color
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        primary = Color.Unspecified,
        primaryVariant = Color.Unspecified,
        secondary = Color.Unspecified,
        secondaryVariant = Color.Unspecified
    )
}

@Composable
fun Share2StorageTheme(
    content: @Composable () -> Unit
) {
    val extendedColors = ExtendedColors(
        primary = Blue500,
        primaryVariant = Blue700,
        secondary = accent,
        secondaryVariant = accentVariant
    )
    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colors = MaterialTheme.colors.copy(
                primary = extendedColors.primary,
                primaryVariant = extendedColors.primaryVariant,
                secondary = extendedColors.secondary,
                secondaryVariant = extendedColors.secondaryVariant
            ),
            content = content
        )
    }
}



