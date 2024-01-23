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

package com.mateusrodcosta.apps.share2storage

import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import com.mateusrodcosta.apps.share2storage.ui.theme.AppTheme
import com.mateusrodcosta.apps.share2storage.utils.SharedPreferenceKeys
import com.mateusrodcosta.apps.share2storage.utils.appTopAppBarColors


class SettingsActivity : ComponentActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private var spSkipFileDetails: Boolean = false
    private var spDefaultSaveLocation: Uri? = null

    private var updateDefaultSaveLocation: ((Uri?) -> Unit)? = null

    private val getSaveLocationDir =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            if (uri == null) return@registerForActivityResult

            Log.d("settings] getSaveLocationDir] uri", uri.toString())
            Log.d("settings] getSaveLocationDir] uri.path", uri.path.toString())
            updateDefaultSaveLocation?.let { it(uri) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        initSharedPreferences()
        setContent { SettingsScreen() }
    }

    private fun initSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        spSkipFileDetails =
            sharedPreferences.getBoolean(SharedPreferenceKeys.skipFileDetailsKey, false)
        Log.d("settings] initSharedPreferences] skipFileDetails", spSkipFileDetails.toString())
        val spDefaultSaveLocationRaw =
            sharedPreferences.getString(SharedPreferenceKeys.defaultSaveLocationKey, null)
        spDefaultSaveLocation = if (spDefaultSaveLocationRaw != null) {
            val uri = Uri.parse(spDefaultSaveLocationRaw)

            Log.d("settings] initSharedPreferences] uri", uri.toString())
            Log.d("settings] initSharedPreferences] uri.path", uri.path.toString())

            uri
        } else {
            null
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    @Preview
    fun SettingsScreen() {
        var skipFileDetails by remember { mutableStateOf(spSkipFileDetails) }
        var defaultSaveLocation by remember { mutableStateOf(spDefaultSaveLocation) }

        val updateSkipFileDetails: (Boolean) -> Unit = { value ->
            sharedPreferences.edit(commit = true) {
                putBoolean(SharedPreferenceKeys.skipFileDetailsKey, value)
            }
            skipFileDetails = value
        }

        updateDefaultSaveLocation = { value ->
            sharedPreferences.edit(commit = true) {
                if (value != null) {
                    putString(SharedPreferenceKeys.defaultSaveLocationKey, value.toString())
                } else {
                    remove(SharedPreferenceKeys.defaultSaveLocationKey)
                }
            }
            if (spDefaultSaveLocation != null) {
                val curDefaultSaveLocation = spDefaultSaveLocation!!
                contentResolver.persistedUriPermissions.forEach {
                    if (it.uri == curDefaultSaveLocation) {
                        val isRead = if (it.isReadPermission) FLAG_GRANT_READ_URI_PERMISSION
                        else 0
                        val isWrite = if (it.isWritePermission) FLAG_GRANT_WRITE_URI_PERMISSION
                        else 0

                        contentResolver.releasePersistableUriPermission(
                            curDefaultSaveLocation, isRead or isWrite
                        )
                    }
                }
            }
            if (value != null) {
                contentResolver.takePersistableUriPermission(
                    value, FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
            defaultSaveLocation = value
        }

        AppTheme {
            Scaffold(topBar = {
                TopAppBar(title = { Text(stringResource(R.string.settings)) },
                    colors = appTopAppBarColors(),
                    navigationIcon = {
                        IconButton(onClick = { finish() }) {
                            Icon(
                                Icons.Filled.ArrowBack, stringResource(id = R.string.back_arrow)
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
                            Row(modifier = Modifier
                                .clickable { updateSkipFileDetails(!skipFileDetails) }
                                .padding(PaddingValues(horizontal = 16.dp, vertical = 8.dp))
                                .heightIn(min = 48.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1.0f)) {
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
                            Row(modifier = Modifier
                                .clickable {
                                    getSaveLocationDir.launch(null)
                                }
                                .padding(PaddingValues(horizontal = 16.dp, vertical = 8.dp))
                                .heightIn(min = 48.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1.0f)) {
                                    Text(
                                        stringResource(id = R.string.settings_default_save_location),
                                        style = MaterialTheme.typography.titleLarge,
                                    )
                                    Text(
                                        defaultSaveLocation?.path
                                            ?: stringResource(R.string.settings_default_save_location_last_used),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                IconButton(onClick = { updateDefaultSaveLocation!!(null) }) {
                                    Icon(Icons.Rounded.Clear, stringResource(R.string.clear_button))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

