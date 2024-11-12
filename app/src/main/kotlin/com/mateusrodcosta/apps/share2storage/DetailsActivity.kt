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
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.mateusrodcosta.apps.share2storage.model.UriData
import com.mateusrodcosta.apps.share2storage.screens.DetailsScreen
import com.mateusrodcosta.apps.share2storage.screens.DetailsScreenSkipped
import com.mateusrodcosta.apps.share2storage.utils.CreateDocumentWithInitialUri
import com.mateusrodcosta.apps.share2storage.utils.SharedPreferenceKeys
import com.mateusrodcosta.apps.share2storage.utils.getUriData
import com.mateusrodcosta.apps.share2storage.utils.saveFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailsActivity : ComponentActivity() {
    private lateinit var createFile: ActivityResultLauncher<String>
    private lateinit var fileUri: Uri
    private lateinit var uriData: UriData

    private var skipFileDetails: Boolean = false
    private var defaultSaveLocation: Uri? = null
    private var shouldShowFilePreview: Boolean = true
    private var shouldSkipFilePicker: Boolean = false


    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        getPreferences()
        handleIntent(intent)
        val launchFilePicker = {
            if (shouldSkipFilePicker) {
                lifecycleScope.launch {
                    val dir = DocumentFile.fromTreeUri(applicationContext, defaultSaveLocation!!)
                    val file = dir!!.createFile(uriData.type, uriData.displayName)

                    if (file?.uri != null) handleFileSave(file.uri, fileUri)
                }
            } else {
                createFile.launch(uriData.displayName)
            }
            Unit
        }

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)

            if (skipFileDetails) {
                LaunchedEffect(key1 = Unit) {
                    launchFilePicker()
                }
                DetailsScreenSkipped()
            } else {
                DetailsScreen(
                    uriData = uriData,
                    windowSizeClass = windowSizeClass,
                    launchFilePicker = launchFilePicker,
                )
            }
        }
    }

    private fun getPreferences() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val skipFileDetails =
            sharedPreferences.getBoolean(SharedPreferenceKeys.SKIP_FILE_DETAILS_KEY, false)
        Log.d("details] skipFileDetails", skipFileDetails.toString())
        this.skipFileDetails = skipFileDetails

        val defaultSaveLocationRaw =
            sharedPreferences.getString(SharedPreferenceKeys.DEFAULT_SAVE_LOCATION_KEY, null)
        Log.d("details] defaultSaveLocationRaw", defaultSaveLocationRaw.toString())
        val defaultSaveLocation =
            if (defaultSaveLocationRaw != null) Uri.parse(defaultSaveLocationRaw)
            else null
        Log.d("details] defaultSaveLocation", defaultSaveLocation.toString())
        this.defaultSaveLocation = defaultSaveLocation

        val showFilePreview =
            sharedPreferences.getBoolean(SharedPreferenceKeys.SHOW_FILE_PREVIEW_KEY, true)
        Log.d("details] showFilePreview", showFilePreview.toString())
        this.shouldShowFilePreview = !skipFileDetails && showFilePreview

        val skipFilePicker =
            sharedPreferences.getBoolean(SharedPreferenceKeys.SKIP_FILE_PICKER, false)
        Log.d("details] skipFilePicker", skipFilePicker.toString())
        // Only skip file picker if both a default folder is set and "Skip File Picker is selected"
        this.shouldSkipFilePicker = defaultSaveLocation != null && skipFilePicker
    }

    private fun handleIntent(intent: Intent?) {
        val fileUri: Uri? = if (intent?.action == Intent.ACTION_SEND) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) intent.getParcelableExtra(
                Intent.EXTRA_STREAM, Uri::class.java
            )
            else @Suppress("DEPRECATION") intent.getParcelableExtra(Intent.EXTRA_STREAM)
        } else if (intent?.action == Intent.ACTION_VIEW) intent.data
        else null
        Log.d("fileUri", "Action: ${intent?.action}, uri: $fileUri")

        if (fileUri == null) return
        this.fileUri = fileUri
        val uriData = getUriData(contentResolver, fileUri, getPreview = shouldShowFilePreview)
        if (uriData == null) return
        this.uriData = uriData

        createFile = registerForActivityResult(
            CreateDocumentWithInitialUri(uriData.type, defaultSaveLocation)
        ) { uri ->
            if (uri == null) {
                if (skipFileDetails) finish()
            } else {
                lifecycleScope.launch {
                    handleFileSave(uri, fileUri)
                }
            }
        }
    }

    private suspend fun handleFileSave(uri: Uri, fileUri: Uri) {
        return withContext(Dispatchers.IO) {
            val isSuccess = saveFile(baseContext, uri, fileUri)

            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                    baseContext, if (isSuccess) {
                        R.string.toast_saved_file_success
                    } else {
                        R.string.toast_saved_file_failure
                    }, Toast.LENGTH_LONG
                ).show()
            }

            if (skipFileDetails || shouldSkipFilePicker) {
                finish()
            }
        }
    }
}