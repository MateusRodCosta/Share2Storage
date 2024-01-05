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

package com.mateusrodcosta.apps.share2storage

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.mateusrodcosta.apps.share2storage.theme.AppTheme
import com.mateusrodcosta.apps.share2storage.utils.spSkipFileDetailsKey


class SettingsActivity : ComponentActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private var spSkipFileDetails: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSharedPreferences()
        setContent { SettingsScreen() }
    }

    private fun initSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        spSkipFileDetails = sharedPreferences.getBoolean(spSkipFileDetailsKey, false)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    @Preview
    fun SettingsScreen() {
        var skipFileDetails by remember { mutableStateOf(spSkipFileDetails) }

        val updateSkipFileDetails: (Boolean) -> Unit = { value ->
            sharedPreferences.edit(commit = true) {
                putBoolean(spSkipFileDetailsKey, value)
            }
            skipFileDetails = value
        }
        AppTheme {
            Scaffold(topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.settings)) },
                    navigationIcon = {
                        IconButton(onClick = { finish() }) {
                            Icon(
                                Icons.Filled.ArrowBack, stringResource(id = R.string.back_arrow)
                            )
                        }
                    },
                )
            }) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Column {
                            Row(modifier = Modifier
                                .clickable { updateSkipFileDetails(!skipFileDetails) }
                                .padding(PaddingValues(horizontal = 16.dp, vertical = 8.dp))
                                .heightIn(min = 48.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                Column(
                                    modifier = Modifier.weight(1.0f),
                                ) {
                                    Text(
                                        stringResource(id = R.string.settings_skip_file_details_page),
                                        style = MaterialTheme.typography.titleLarge,
                                    )
                                    Text(
                                        stringResource(R.string.settings_skip_file_details_page_info),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Switch(
                                    checked = skipFileDetails,
                                    onCheckedChange = updateSkipFileDetails
                                )

                            }
                        }
                    }
                }
            }
        }
    }
}

