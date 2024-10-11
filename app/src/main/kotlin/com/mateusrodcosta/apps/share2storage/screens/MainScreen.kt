/*
 *     Copyright (C) 2022 - 2024 Mateus Rodrigues Costa
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

package com.mateusrodcosta.apps.share2storage.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mateusrodcosta.apps.share2storage.R
import com.mateusrodcosta.apps.share2storage.screens.shared.AppBasicDivider
import com.mateusrodcosta.apps.share2storage.screens.shared.AppListHeader
import com.mateusrodcosta.apps.share2storage.screens.shared.shouldShowLandscape
import com.mateusrodcosta.apps.share2storage.ui.theme.AppTheme

@Preview(apiLevel = 34, showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreenContent(
        widthSizeClass = WindowWidthSizeClass.Compact,
        heightSizeClass = WindowHeightSizeClass.Medium,
    )
}

@Composable
fun MainScreen(navController: NavController, windowSizeClass: WindowSizeClass) {
    MainScreenContent(
        navController = navController,
        widthSizeClass = windowSizeClass.widthSizeClass,
        heightSizeClass = windowSizeClass.heightSizeClass,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
    navController: NavController? = null,
    widthSizeClass: WindowWidthSizeClass,
    heightSizeClass: WindowHeightSizeClass,
) {
    AppTheme {
        Scaffold(topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { navController?.navigate("settings") }) {
                        Icon(Icons.Rounded.Settings, stringResource(R.string.settings))
                    }
                },
            )
        }) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                val showLandscape = shouldShowLandscape(widthSizeClass, heightSizeClass)
                if (showLandscape) HowToUseLandscape()
                else HowToUsePortrait()
            }
        }
    }
}

@Composable
fun HowToUseLandscape() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.weight(1.0f)) {
            HowToUseHeader()
        }
        Box(modifier = Modifier.weight(3.0f)) {
            HowToUseContent(isLandscape = true)
        }
    }
}

@Composable
fun HowToUsePortrait() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HowToUseHeader()
        HowToUseContent()
    }
}

@Composable
fun HowToUseHeader() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painterResource(R.drawable.ic_launcher_foreground),
            stringResource(R.string.app_name),
            modifier = Modifier.scale(1.8f),
        )
        Text(
            stringResource(R.string.how_to_use),
            style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.secondary),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun HowToUseContent(isLandscape: Boolean = false) {
    Column(
        modifier = if (isLandscape) Modifier.verticalScroll(rememberScrollState()) else Modifier.fillMaxSize()
    ) {
        HowToUseRow(1, stringResource(R.string.how_to_use_step_1))
        HowToUseRow(
            2, String.format(
                stringResource(R.string.how_to_use_step_2), stringResource(
                    R.string.app_name
                )
            )
        )
        HowToUseRow(3, stringResource(R.string.how_to_use_step_3))
        HowToUseRow(4, stringResource(R.string.how_to_use_step_4))
        AppBasicDivider()
        AppListHeader(title = stringResource(R.string.how_to_use_about_title))
        AboutRowWithURL(
            stringResource(R.string.how_to_use_app_donation),
            url = stringResource(R.string.donation_url),
            "%s"
        )
        AboutRowWithURL(
            stringResource(R.string.how_to_use_about),
            url = stringResource(R.string.github_profile_url),
            "Mateus Rodrigues Costa",
            false
        )
        AboutRow(
            stringResource(R.string.how_to_use_app_icon_credits)
        )
        AboutRowWithURL(
            stringResource(R.string.how_to_use_github),
            url = stringResource(R.string.source_code_url),
            "%s"
        )
    }
}

@Composable
fun HowToUseRow(num: Int?, string: String) {
    ListItem(modifier = Modifier.clickable { }, leadingContent = {
        Text(
            if (num == null) "   " else "$num.",
            style = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold),
        )
    }, headlineContent = {
        Text(string, softWrap = true)
    })
}

@Composable
fun AboutRow(string: String) {
    ListItem(modifier = Modifier.clickable { }, headlineContent = {
        Text(string, softWrap = true)
    })
}

@Composable
fun AboutRowWithURL(
    string: String, url: String, linkPlacement: String, replaceWithUrl: Boolean = true
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