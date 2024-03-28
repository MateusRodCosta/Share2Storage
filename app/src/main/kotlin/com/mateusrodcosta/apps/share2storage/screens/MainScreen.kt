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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mateusrodcosta.apps.share2storage.R
import com.mateusrodcosta.apps.share2storage.screens.shared.AppBasicDivider
import com.mateusrodcosta.apps.share2storage.screens.shared.AppListHeader
import com.mateusrodcosta.apps.share2storage.screens.shared.appTopAppBarColors
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
                colors = appTopAppBarColors(),
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
    Row {
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
            style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.tertiary),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun HowToUseContent(isLandscape: Boolean = false) {
    Column(
        modifier = if (isLandscape) Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) else Modifier.fillMaxSize()
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
        AboutRow(
            stringResource(R.string.how_to_use_about),
            url = stringResource(R.string.github_profile_url)
        )
        AboutRow(
            String.format(
                stringResource(R.string.how_to_use_github), stringResource(

                    R.string.source_code_url
                )
            ), url = stringResource(R.string.source_code_url)
        )
        AboutRow(
            stringResource(R.string.how_to_use_app_icon_credits), url = null
        )
        AboutRow(
            String.format(
                stringResource(R.string.how_to_use_app_donation), stringResource(
                    R.string.donation_url
                )
            ), url = stringResource(R.string.donation_url)
        )
    }
}

@Composable
fun HowToUseRow(num: Int?, string: String) {
    GenericRow {
        Text(
            if (num == null) "   " else "$num.",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            string,
            style = MaterialTheme.typography.bodyLarge,
            softWrap = true,
        )
    }
}

@Composable
fun AboutRow(string: String, url: String?) {
    val localUriHandler = LocalUriHandler.current
    GenericRow(onClick = { if (url != null) localUriHandler.openUri(url) }) {
        Text(
            string,
            style = MaterialTheme.typography.bodyLarge,
            softWrap = true,
        )
    }
}

@Composable
fun GenericRow(onClick: () -> Unit = {}, content: @Composable() (RowScope.() -> Unit)) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }) {
        Row(
            modifier = Modifier
                .padding(PaddingValues(horizontal = 16.dp, vertical = 8.dp))
                .heightIn(min = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}