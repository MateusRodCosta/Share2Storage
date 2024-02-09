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

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.PreferenceManager
import com.mateusrodcosta.apps.share2storage.screens.SettingsScreen
import com.mateusrodcosta.apps.share2storage.utils.SharedPreferenceKeys

class SettingsActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel = SettingsViewModel()

    private val getSaveLocationDirIntent =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            if (uri == null) return@registerForActivityResult

            Log.d("settings] getSaveLocationDir] uri", uri.toString())
            Log.d("settings] getSaveLocationDir] uri.path", uri.path.toString())

            settingsViewModel.updateDefaultSaveLocation(this, uri)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val spDefaultSaveLocationRaw =
            sharedPreferences.getString(SharedPreferenceKeys.defaultSaveLocationKey, null)

        val spDefaultSaveLocation = if (spDefaultSaveLocationRaw != null) {
            try {
                val uri = Uri.parse(spDefaultSaveLocationRaw)

                Log.d("settings] initSharedPreferences] uri", uri.toString())
                Log.d("settings] initSharedPreferences] uri.path", uri.path.toString())

                uri
            } catch (_: Exception) {
                null
            }
        } else {
            null
        }

        val spSkipFileDetails =
            sharedPreferences.getBoolean(SharedPreferenceKeys.skipFileDetailsKey, false)
        Log.d("settings] initSharedPreferences] skipFileDetails", spSkipFileDetails.toString())

        settingsViewModel.initDefaultSaveLocation(spDefaultSaveLocation)
        settingsViewModel.initSkipFileDetails(spSkipFileDetails)

        setContent {
            SettingsScreen(
                spDefaultSaveLocation = settingsViewModel.defaultSaveLocation,
                spSkipFileDetails = settingsViewModel.skipFileDetails,
                launchFilePicker = { getSaveLocationDirIntent.launch(null) },
                clearSaveDirectory = { settingsViewModel.clearSaveDirectory(this) },
                updateSkipFileDetails = { value: Boolean ->
                    settingsViewModel.updateSkipFileDetails(this, value)
                },
                activity = this
            )
        }
    }
}
