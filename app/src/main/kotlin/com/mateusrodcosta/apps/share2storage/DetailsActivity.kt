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

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.preference.PreferenceManager
import com.mateusrodcosta.apps.share2storage.model.UriData
import com.mateusrodcosta.apps.share2storage.screens.DetailsScreen
import com.mateusrodcosta.apps.share2storage.utils.CreateDocumentWithInitialUri
import com.mateusrodcosta.apps.share2storage.utils.SharedPreferenceKeys
import com.mateusrodcosta.apps.share2storage.utils.getUriData
import com.mateusrodcosta.apps.share2storage.utils.saveFile

class DetailsActivity : ComponentActivity() {
    private var createFile: ActivityResultLauncher<String>? = null
    private var uriData: UriData? = null

    private var skipFileDetails: Boolean? = null
    private var defaultSaveLocation: Uri? = null

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        getPreferences()
        handleIntent()
        val launchFilePicker = {
            createFile?.launch(uriData?.displayName ?: "")
        }

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            DetailsScreen(
                uriData = uriData,
                widthSizeClass = windowSizeClass.widthSizeClass,
                heightSizeClass = windowSizeClass.heightSizeClass,
                launchFilePicker = launchFilePicker,
            )
        }

        if (skipFileDetails == true) launchFilePicker()
    }

    private fun getPreferences() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        skipFileDetails =
            sharedPreferences.getBoolean(SharedPreferenceKeys.skipFileDetailsKey, false)
        val defaultSaveLocationRaw =
            sharedPreferences.getString(SharedPreferenceKeys.defaultSaveLocationKey, null)
        Log.d("details] defaultSaveLocationRaw", defaultSaveLocationRaw.toString())
        defaultSaveLocation = if (defaultSaveLocationRaw != null) Uri.parse(defaultSaveLocationRaw)
        else null

        Log.d("details] defaultSaveLocation", defaultSaveLocation.toString())
    }

    private fun handleIntent() {
        if (intent?.action == Intent.ACTION_SEND) {

            val fileUri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
            } else {
                @Suppress("DEPRECATION") intent.getParcelableExtra(Intent.EXTRA_STREAM)
            }

            Log.d("fileUri", fileUri.toString())

            uriData = getUriData(contentResolver, fileUri)

            if (uriData != null) {
                createFile = registerForActivityResult(
                    CreateDocumentWithInitialUri(uriData?.type ?: "*/*", defaultSaveLocation)
                ) { uri ->
                    if (uri == null || fileUri == null) return@registerForActivityResult
                    val isSuccess = saveFile(baseContext, uri, fileUri)
                    Toast.makeText(
                        baseContext, if (isSuccess) {
                            R.string.toast_saved_file_success
                        } else {
                            R.string.toast_saved_file_failure
                        }, Toast.LENGTH_LONG
                    ).show()
                    if (skipFileDetails == true) finish()
                }
            }
        }
    }
}