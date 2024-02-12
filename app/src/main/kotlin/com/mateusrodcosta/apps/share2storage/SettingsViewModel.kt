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

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.mateusrodcosta.apps.share2storage.utils.SharedPreferenceKeys
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var contentResolver: ContentResolver
    private lateinit var getSaveLocationDirIntent: ActivityResultLauncher<Uri?>

    private val _defaultSaveLocation = MutableStateFlow<Uri?>(null)
    val defaultSaveLocation: StateFlow<Uri?> = _defaultSaveLocation

    private val _skipFileDetails = MutableStateFlow(false)
    val skipFileDetails: StateFlow<Boolean> = _skipFileDetails

    fun assignSaveLocationDirIntent(intent: ActivityResultLauncher<Uri?>) {
        getSaveLocationDirIntent = intent
    }

    fun getSaveLocationDirIntent(): ActivityResultLauncher<Uri?> {
        return getSaveLocationDirIntent
    }

    fun receiveContext(context: Context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        contentResolver = context.contentResolver
    }

    fun initPreferences() {
        val spDefaultSaveLocationRaw =
            sharedPreferences.getString(SharedPreferenceKeys.defaultSaveLocationKey, null)
        val spDefaultSaveLocation = if (spDefaultSaveLocationRaw != null) try {
            val uri = Uri.parse(spDefaultSaveLocationRaw)

            Log.d("settings] initSharedPreferences] uri", uri.toString())
            Log.d("settings] initSharedPreferences] uri.path", uri.path.toString())

            uri
        } catch (_: Exception) {
            null
        }
        else null

        val spSkipFileDetails =
            sharedPreferences.getBoolean(SharedPreferenceKeys.skipFileDetailsKey, false)
        Log.d("settings] initSharedPreferences] skipFileDetails", spSkipFileDetails.toString())

        _defaultSaveLocation.value = spDefaultSaveLocation
        _skipFileDetails.value = spSkipFileDetails
    }

    fun updateDefaultSaveLocation(value: Uri?) {
        val currentSaveLocationRaw =
            sharedPreferences.getString(SharedPreferenceKeys.defaultSaveLocationKey, null)

        sharedPreferences.edit(commit = true) {
            if (value != null) putString(
                SharedPreferenceKeys.defaultSaveLocationKey, value.toString()
            )
            else remove(SharedPreferenceKeys.defaultSaveLocationKey)

        }

        val currentSaveLocation: Uri? = if (currentSaveLocationRaw != null) try {
            Uri.parse(currentSaveLocationRaw)
        } catch (_: Exception) {
            null
        }
        else null

        if (currentSaveLocation != null) {
            contentResolver.persistedUriPermissions.forEach {
                if (it.uri == currentSaveLocation) {
                    val isRead = if (it.isReadPermission) Intent.FLAG_GRANT_READ_URI_PERMISSION
                    else 0
                    val isWrite = if (it.isWritePermission) Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    else 0

                    contentResolver.releasePersistableUriPermission(
                        currentSaveLocation, isRead or isWrite
                    )
                }
            }
        }

        if (value != null) contentResolver.takePersistableUriPermission(
            value, Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )

        _defaultSaveLocation.value = value
    }

    fun clearSaveDirectory() {
        updateDefaultSaveLocation(null)
    }

    fun updateSkipFileDetails(value: Boolean) {
        sharedPreferences.edit(commit = true) {
            putBoolean(SharedPreferenceKeys.skipFileDetailsKey, value)
        }

        _skipFileDetails.value = value
    }
}