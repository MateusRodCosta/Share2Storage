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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.mateusrodcosta.apps.share2storage.model.UriData
import com.mateusrodcosta.apps.share2storage.screens.DetailsScreen
import com.mateusrodcosta.apps.share2storage.utils.CreateDocumentWithInitialUri
import com.mateusrodcosta.apps.share2storage.utils.SharedPreferenceKeys
import com.mateusrodcosta.apps.share2storage.utils.getUriData
import com.mateusrodcosta.apps.share2storage.utils.saveFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class DetailsActivity : ComponentActivity() {
    private lateinit var createFile: ActivityResultLauncher<String>
    private var uriData: UriData? = null

    private var skipFileDetails: Boolean = false
    private var defaultSaveLocation: Uri? = null
    private var showFilePreview: Boolean = true

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        getPreferences()
        handleIntent(intent)
        val launchFilePicker = {
            createFile.launch(uriData?.displayName ?: "")
        }

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)

            DetailsScreen(
                uriData = uriData,
                windowSizeClass = windowSizeClass,
                launchFilePicker = launchFilePicker,
            )
        }

        if (skipFileDetails) launchFilePicker()
    }

    private fun getPreferences() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        skipFileDetails =
            sharedPreferences.getBoolean(SharedPreferenceKeys.SKIP_FILE_DETAILS_KEY, false)
        Log.d("details] skipFileDetails", skipFileDetails.toString())

        val defaultSaveLocationRaw =
            sharedPreferences.getString(SharedPreferenceKeys.DEFAULT_SAVE_LOCATION_KEY, null)
        Log.d("details] defaultSaveLocationRaw", defaultSaveLocationRaw.toString())
        defaultSaveLocation = if (defaultSaveLocationRaw != null) Uri.parse(defaultSaveLocationRaw)
        else null
        Log.d("details] defaultSaveLocation", defaultSaveLocation.toString())

        showFilePreview =
            sharedPreferences.getBoolean(SharedPreferenceKeys.SHOW_FILE_PREVIEW_KEY, true)
        Log.d("details] showFilePreview", showFilePreview.toString())
    }

    private fun handleIntent(intent: Intent?) {
        var fileUri: Uri? = null
        if (intent?.action == Intent.ACTION_SEND) {
            fileUri =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) intent.getParcelableExtra(
                    Intent.EXTRA_STREAM, Uri::class.java
                )
                else @Suppress("DEPRECATION") intent.getParcelableExtra(Intent.EXTRA_STREAM)
            Log.d("fileUri ACTION_SEND", fileUri.toString())
        }
        // ACTION_VIEW intents interceptor
        if (intent?.action == Intent.ACTION_VIEW) {
            fileUri = intent.data
            Log.d("fileUri ACTION_VIEW", fileUri.toString())
        }

        if (fileUri != null) uriData =
            getUriData(contentResolver, fileUri, getPreview = showFilePreview)
        if (uriData != null) {
            createFile = registerForActivityResult(
                CreateDocumentWithInitialUri(uriData?.type ?: "*/*", defaultSaveLocation)
            ) { uri ->
                lifecycleScope.launch {
                    handleFileSave(uri, fileUri)
                }
            }
        }
    }

    private suspend fun handleFileSave(uri: Uri?, fileUri: Uri?) {
        if (uri == null || fileUri == null) return
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

            if (skipFileDetails) {
                delay(1.toDuration(DurationUnit.SECONDS))
                finish()
            }
        }

    }
}