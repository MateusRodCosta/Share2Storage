/*
 *     Copyright (C) 2022 - 2023 Mateus Rodrigues Costa
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.mateusrodcosta.apps.share2storage.screens.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AppBasicDivider() {
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = Dp.Hairline)
}

@Composable
fun AppListHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth(),
        style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.secondary),
    )
}

@Composable
fun ListItemWithURL(
    string: String, linkPlacement: String, url: String, replaceWithUrl: Boolean = false
) {
    val localUriHandler = LocalUriHandler.current
    val stringPieces = string.split(linkPlacement)

    ListItem(modifier = Modifier.clickable { localUriHandler.openUri(url) }, headlineContent = {
        Text(
            buildAnnotatedString {
                append(stringPieces[0])
                withStyle(
                    style = SpanStyle(
                        color = Color.Blue, textDecoration = TextDecoration.Underline
                    )
                ) {
                    if (replaceWithUrl) append(url) else append(linkPlacement)
                }
                if (stringPieces.size >= 2) append(stringPieces[1])
            },
            softWrap = true,
        )
    })
}