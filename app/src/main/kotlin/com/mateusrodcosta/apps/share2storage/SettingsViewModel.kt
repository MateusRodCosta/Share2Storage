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

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.mateusrodcosta.apps.share2storage.utils.SharedPreferenceKeys
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class SettingsViewModel : ViewModel() {

    private val _defaultSaveLocation = MutableStateFlow<Uri?>(null)
    val defaultSaveLocation: StateFlow<Uri?> = _defaultSaveLocation

    private val _skipFileDetails = MutableStateFlow(false)
    val skipFileDetails: StateFlow<Boolean> = _skipFileDetails

    fun initDefaultSaveLocation(value: Uri?) {
        _defaultSaveLocation.value = value
    }

    fun initSkipFileDetails(value: Boolean) {
        _skipFileDetails.value = value
    }

    fun updateDefaultSaveLocation(context: Context, value: Uri?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val contentResolver = context.contentResolver

        val currentSaveLocationRaw =
            sharedPreferences.getString(SharedPreferenceKeys.defaultSaveLocationKey, null)

        sharedPreferences?.edit(commit = true) {
            if (value != null) {
                putString(SharedPreferenceKeys.defaultSaveLocationKey, value.toString())
            } else {
                remove(SharedPreferenceKeys.defaultSaveLocationKey)
            }
        }

        val currentSaveLocation: Uri? = if (currentSaveLocationRaw != null) {
            try {
                Uri.parse(currentSaveLocationRaw)
            } catch (_: Exception) {
                null
            }
        } else null

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

        if (value != null) {
            contentResolver.takePersistableUriPermission(
                value, Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        }

        _defaultSaveLocation.value = value
    }

    fun clearSaveDirectory(context: Context) {
        updateDefaultSaveLocation(context, null)
    }

    fun updateSkipFileDetails(context: Context, value: Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit(commit = true) {
            putBoolean(SharedPreferenceKeys.skipFileDetailsKey, value)
        }

        _skipFileDetails.value = value
    }
}