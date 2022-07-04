/*
 *     Copyright (C) 2022 Mateus Rodrigues Costa
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>
 */

package com.mateusrodcosta.apps.share2storage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.format.Formatter
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mateusrodcosta.apps.share2storage.utils.Share2StorageTheme

class ShareActivity : ComponentActivity() {

    private var srcUri: Uri? = null
    private var createFile: ActivityResultLauncher<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent != null) {
            if (intent.action == Intent.ACTION_SEND) {
                srcUri = intent.extras?.get(Intent.EXTRA_STREAM) as Uri?
                Log.d("srcUri", srcUri.toString())
                if(srcUri != null) {
                    val source = srcUri as Uri
                    val type = contentResolver.getType(source)
                    createFile = registerForActivityResult(
                        CreateDocument(type ?: "*/*"),
                        fun(uri: Uri?) {
                            if (uri == null) return

                            saveFile(baseContext, uri, source)
                        })
                }
            }
        }
        setContent {
            ShareScreen(srcUri = srcUri)
        }
    }

    @Composable
    fun ShareScreen(srcUri: Uri?) {
        Share2StorageTheme {
            Scaffold(topBar = {
                TopAppBar(
                    title = {
                        Text(stringResource(R.string.app_name))
                    },
                )
            }) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (srcUri != null) {
                        val uriData = getUriData(contentResolver, srcUri)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Button(onClick = {
                                createFile?.launch(uriData?.displayName ?: "")
                            }) {
                                Text(
                                    stringResource(R.string.save_button)
                                )
                            }
                            Row {
                                Text(stringResource(R.string.file_name) + ": ")
                                Text(uriData?.displayName ?: stringResource(R.string.unknown))
                            }
                            Row {
                                Text(stringResource(R.string.file_type) + ": ")
                                Text(
                                    contentResolver.getType(srcUri) ?: stringResource(R.string.unknown)
                                )
                            }
                            Row {
                                Text(stringResource(R.string.file_size) + ": ")
                                Text(
                                    if (uriData?.size != null) Formatter.formatShortFileSize(
                                        baseContext,
                                        uriData.size
                                    ) else stringResource(R.string.unknown)
                                )
                            }
                        }
                    } else Text(stringResource(R.string.no_file_found))
                }
            }
        }
    }


}