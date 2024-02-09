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

import android.text.format.Formatter
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AudioFile
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.VideoFile
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.mateusrodcosta.apps.share2storage.R
import com.mateusrodcosta.apps.share2storage.model.SampleUriDataProvider
import com.mateusrodcosta.apps.share2storage.model.UriData
import com.mateusrodcosta.apps.share2storage.ui.theme.AppTheme
import com.mateusrodcosta.apps.share2storage.utils.AppBasicDivider
import com.mateusrodcosta.apps.share2storage.utils.appTopAppBarColors

@Preview(apiLevel = 33, showBackground = true)
@Composable
fun DetailsScreenPreview(@PreviewParameter(SampleUriDataProvider::class) uriData: UriData?) {
    DetailsScreen(
        uriData = uriData,
        widthSizeClass = WindowWidthSizeClass.Compact,
        heightSizeClass = WindowHeightSizeClass.Medium,
        launchFilePicker = {},
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    uriData: UriData?,
    widthSizeClass: WindowWidthSizeClass,
    heightSizeClass: WindowHeightSizeClass,
    launchFilePicker: () -> Unit?,
) {
    AppTheme {
        Scaffold(topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.file_details)) },
                colors = appTopAppBarColors(),
            )
        }, floatingActionButton = {
            if (uriData != null) {
                FloatingActionButton(
                    onClick = { launchFilePicker() },
                    content = {
                        Icon(
                            Icons.Rounded.Download,
                            contentDescription = stringResource(R.string.save_button)
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                )
            }
        }) { paddingValues ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                val showLandscapePhone = heightSizeClass == WindowHeightSizeClass.Compact
                val showLandscapeTablet = widthSizeClass == WindowWidthSizeClass.Expanded
                if (uriData != null) {
                    if (showLandscapePhone || showLandscapeTablet) {
                        FileDetailsLandscape(uriData)
                    } else {
                        FileDetailsPortrait(uriData)
                    }
                } else Text(
                    stringResource(R.string.no_file_found),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun FileDetailsPortrait(uriData: UriData) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(modifier = Modifier.weight(1.0f)) {
            FilePreview(uriData)
        }
        Box(modifier = Modifier.weight(1.0f)) {
            FileInfo(uriData)
        }
    }
}

@Composable
fun FileDetailsLandscape(uriData: UriData) {
    Row(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(modifier = Modifier.weight(1.0f)) {
            FilePreview(uriData)
        }
        Box(modifier = Modifier.weight(1.0f)) {
            FileInfo(uriData)
        }
    }
}

@Composable
fun FileInfo(uriData: UriData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AppBasicDivider()
        FileInfoLine(
            label = stringResource(R.string.file_name),
            content = uriData.displayName ?: stringResource(R.string.unknown)
        )
        AppBasicDivider()
        FileInfoLine(
            label = stringResource(R.string.file_type),
            content = uriData.type ?: stringResource(R.string.unknown)
        )
        AppBasicDivider()
        FileInfoLine(
            label = stringResource(R.string.file_size),
            // TODO: Find code to calculate file size for previews
            content = if (uriData.size != null) Formatter.formatFileSize(
                LocalContext.current, uriData.size
            ) else stringResource(R.string.unknown)
        )
        AppBasicDivider()
    }
}

@Composable
fun FileInfoLine(label: String, content: String) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable { }) {
        Column(
            modifier = Modifier
                .padding(PaddingValues(horizontal = 16.dp, vertical = 8.dp))
                .heightIn(min = 48.dp), horizontalAlignment = Alignment.Start
        ) {
            Text(label, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(content, softWrap = true, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun FilePreview(uriData: UriData) {
    val mimeType = uriData.type ?: "*/*"
    val fallbackFileIcon = if (mimeType.startsWith("image/")) {
        Icons.Outlined.Image
    } else if (mimeType.startsWith("audio/")) {
        Icons.Outlined.AudioFile
    } else if (mimeType.startsWith("video/")) {
        Icons.Outlined.VideoFile
    } else {
        Icons.Outlined.Description
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (uriData.previewImage != null) {
            Image(
                modifier = Modifier.align(Alignment.Center),
                bitmap = uriData.previewImage.asImageBitmap(),
                contentDescription = stringResource(R.string.app_name),
                contentScale = ContentScale.Fit,
            )
        } else {
            Icon(
                modifier = Modifier
                    .size(128.dp)
                    .align(Alignment.Center),
                imageVector = fallbackFileIcon,
                contentDescription = stringResource(R.string.app_name),
                tint = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}