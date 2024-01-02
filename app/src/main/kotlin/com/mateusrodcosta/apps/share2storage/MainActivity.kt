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

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.mateusrodcosta.apps.share2storage.theme.AppTheme
import com.mateusrodcosta.apps.share2storage.utils.AppBasicDivider


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MainScreen() }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    @Preview
    fun MainScreen() {
        val context = this
        AppTheme {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(stringResource(R.string.app_name)) }
                    )
                }
            )
            { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Column(modifier = Modifier.padding(all = 16.dp)) {
                            Image(
                                painterResource(R.drawable.ic_launcher_foreground),
                                stringResource(R.string.app_name),
                                modifier = Modifier.scale(2.0f),
                            )
                            Text(
                                stringResource(R.string.how_to_use),
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                        Column {
                            AppBasicDivider()
                            HowToUseRow(1, stringResource(R.string.how_to_use_step_1))
                            AppBasicDivider()
                            HowToUseRow(2, stringResource(R.string.how_to_use_step_2))
                            AppBasicDivider()
                            HowToUseRow(3, stringResource(R.string.how_to_use_step_3))
                            AppBasicDivider()
                            HowToUseRow(4, stringResource(R.string.how_to_use_step_4))
                            AppBasicDivider()
                            Spacer(modifier = Modifier.defaultMinSize(minHeight = (48 + 16).dp))
                            Box(modifier = Modifier.padding(start = 16.dp, end = 8.dp)) {
                                Text(
                                    stringResource(id = R.string.how_to_use_about_title),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            AppBasicDivider()
                            AboutRow(
                                stringResource(R.string.how_to_use_about),
                                url = "https://github.com/MateusRodCosta/",
                                context = context
                            )
                            AppBasicDivider()
                            AboutRow(
                                stringResource(R.string.how_to_use_github),
                                url = "https://github.com/MateusRodCosta/Share2Storage",
                                context = context
                            )
                            AppBasicDivider()
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun HowToUseRow(num: Int?, string: String) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .clickable { }) {
            Row(
                modifier = Modifier
                    .padding(PaddingValues(horizontal = 16.dp, vertical = 8.dp))
                    .heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (num == null) "   " else "$num.",
                    style = MaterialTheme.typography.titleSmall,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    string,
                    style = MaterialTheme.typography.bodyLarge,
                    softWrap = true,
                )
            }
        }
    }

    @Composable
    fun AboutRow(string: String, url: String?, context: Context) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (url != null) {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(context, browserIntent, null)
                }
            }) {
            Row(
                modifier = Modifier
                    .padding(PaddingValues(horizontal = 16.dp, vertical = 8.dp))
                    .heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    string,
                    style = MaterialTheme.typography.bodyLarge,
                    softWrap = true,
                )
            }
        }
    }

}

