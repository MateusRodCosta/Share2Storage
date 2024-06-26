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

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mateusrodcosta.apps.share2storage.R
import com.mateusrodcosta.apps.share2storage.screens.shared.AppBasicDivider
import com.mateusrodcosta.apps.share2storage.screens.shared.AppListHeader
import com.mateusrodcosta.apps.share2storage.screens.shared.appTopAppBarColors
import com.mateusrodcosta.apps.share2storage.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Preview(apiLevel = 34, showBackground = true)
@Composable
fun SettingsScreenPreview() {
    val mockDefaultSaveLocation = MutableStateFlow(null)
    val mockSkipFileDetails = MutableStateFlow(false)
    val mockInterceptActionViewIntents = MutableStateFlow(false)
    val mockShowFilePreview = MutableStateFlow(true)

    SettingsScreenContent(
        spDefaultSaveLocation = mockDefaultSaveLocation,
        spSkipFileDetails = mockSkipFileDetails,
        spInterceptActionViewIntents = mockInterceptActionViewIntents,
        spShowFilePreview = mockShowFilePreview,
    )
}

@Composable
fun SettingsScreen(navController: NavController, settingsViewModel: SettingsViewModel) {
    SettingsScreenContent(
        navController = navController,
        spDefaultSaveLocation = settingsViewModel.defaultSaveLocation,
        spSkipFileDetails = settingsViewModel.skipFileDetails,
        spInterceptActionViewIntents = settingsViewModel.interceptActionViewIntents,
        spShowFilePreview = settingsViewModel.showFilePreview,
        launchFilePicker = { settingsViewModel.getSaveLocationDirIntent().launch(null) },
        clearSaveDirectory = { settingsViewModel.clearSaveDirectory() },
        updateSkipFileDetails = { value: Boolean ->
            settingsViewModel.updateSkipFileDetails(value)
        },
        updateInterceptActionViewIntents = { value: Boolean ->
            settingsViewModel.updateInterceptActionViewIntents(value)
        },
        updateShowFilePreview = { value ->
            settingsViewModel.updateShowFilePreview(value)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    navController: NavController? = null,
    spDefaultSaveLocation: StateFlow<Uri?>,
    spSkipFileDetails: StateFlow<Boolean>,
    spInterceptActionViewIntents: StateFlow<Boolean>,
    spShowFilePreview: StateFlow<Boolean>,
    launchFilePicker: () -> Unit = {},
    clearSaveDirectory: () -> Unit = {},
    updateSkipFileDetails: (Boolean) -> Unit = {},
    updateInterceptActionViewIntents: (Boolean) -> Unit = {},
    updateShowFilePreview: (Boolean) -> Unit = {},
) {
    val settingsPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)

    AppTheme {
        Scaffold(topBar = {
            TopAppBar(title = { Text(stringResource(R.string.settings)) },
                colors = appTopAppBarColors(),
                navigationIcon = {
                    IconButton(onClick = { navController?.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back_arrow)
                        )
                    }
                })
        }) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Column {
                        AppListHeader(title = stringResource(R.string.settings_category_file_picker))
                        DefaultSaveLocationSetting(
                            launchFilePicker = launchFilePicker,
                            clearSaveDirectory = clearSaveDirectory,
                            spDefaultSaveLocation = spDefaultSaveLocation,
                            paddingValues = settingsPadding,
                        )
                        AppBasicDivider()
                        AppListHeader(stringResource(R.string.settings_category_file_details))
                        SkipFileDetailsSetting(
                            updateSkipFileDetails = updateSkipFileDetails,
                            spSkipFileDetails = spSkipFileDetails,
                            paddingValues = settingsPadding,
                        )
                        ShowFilePreviewSetting(
                            updateShowFilePreview = updateShowFilePreview,
                            spShowFilePreview = spShowFilePreview,
                            paddingValues = settingsPadding,
                        )
                        AppBasicDivider()
                        AppListHeader(title = stringResource(R.string.settings_category_intents))
                        InterceptActionViewIntentsSetting(
                            updateInterceptActionViewIntents = updateInterceptActionViewIntents,
                            spInterceptActionViewIntents = spInterceptActionViewIntents,
                            paddingValues = settingsPadding,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DefaultSaveLocationSetting(
    launchFilePicker: () -> Unit,
    clearSaveDirectory: () -> Unit,
    spDefaultSaveLocation: StateFlow<Uri?>,
    paddingValues: PaddingValues,
) {
    val defaultSaveLocation by spDefaultSaveLocation.collectAsState()

    Row(modifier = Modifier
        .clickable { launchFilePicker() }
        .padding(paddingValues)
        .heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1.0f)) {
            Text(
                stringResource(R.string.settings_default_save_location),
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                defaultSaveLocation?.path
                    ?: stringResource(R.string.settings_default_save_location_last_used),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        IconButton(onClick = { clearSaveDirectory() }) {
            Icon(Icons.Rounded.Clear, stringResource(R.string.clear_button))
        }
    }
}

@Composable
fun SkipFileDetailsSetting(
    updateSkipFileDetails: (Boolean) -> Unit,
    spSkipFileDetails: StateFlow<Boolean>,
    paddingValues: PaddingValues,
) {
    val skipFileDetails by spSkipFileDetails.collectAsState()

    Row(modifier = Modifier
        .clickable { updateSkipFileDetails(!skipFileDetails) }
        .padding(paddingValues)
        .heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1.0f)) {
            Text(
                stringResource(R.string.settings_skip_file_details_page),
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                stringResource(R.string.settings_skip_file_details_page_info),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(checked = skipFileDetails, onCheckedChange = { value ->
            updateSkipFileDetails(value)
        })
    }
}


@Composable
fun ShowFilePreviewSetting(
    updateShowFilePreview: (Boolean) -> Unit,
    spShowFilePreview: StateFlow<Boolean>,
    paddingValues: PaddingValues,
) {
    val showFilePreview by spShowFilePreview.collectAsState()

    Row(modifier = Modifier
        .clickable { updateShowFilePreview(!showFilePreview) }
        .padding(paddingValues)
        .heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1.0f)) {
            Text(
                stringResource(R.string.settings_show_file_preview),
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                stringResource(R.string.settings_show_file_preview_info),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(checked = showFilePreview, onCheckedChange = { value ->
            updateShowFilePreview(value)
        })
    }
}

@Composable
fun InterceptActionViewIntentsSetting(
    updateInterceptActionViewIntents: (Boolean) -> Unit,
    spInterceptActionViewIntents: StateFlow<Boolean>,
    paddingValues: PaddingValues,
) {
    val interceptActionViewIntents by spInterceptActionViewIntents.collectAsState()

    Row(modifier = Modifier
        .clickable { updateInterceptActionViewIntents(!interceptActionViewIntents) }
        .padding(paddingValues)
        .heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1.0f)) {
            Text(
                stringResource(R.string.settings_intercept_action_view_intents),
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                stringResource(R.string.settings_intercept_action_view_intents_info),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(checked = interceptActionViewIntents, onCheckedChange = { value ->
            updateInterceptActionViewIntents(value)
        })
    }
}