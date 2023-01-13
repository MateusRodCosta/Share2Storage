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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mateusrodcosta.apps.share2storage.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    @Preview
    fun MainScreen() {
        AppTheme {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(stringResource(R.string.app_name))
                        },
                    )
                },
            ) { paddingValues ->
                Box(
                    modifier = Modifier.padding(paddingValues)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
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
                            BasicDivider()
                            HowToUseRow(1, stringResource(id = R.string.how_to_use_step_1))
                            BasicDivider()
                            HowToUseRow(2, stringResource(id = R.string.how_to_use_step_2))
                            BasicDivider()
                            HowToUseRow(3, stringResource(id = R.string.how_to_use_step_3))
                            BasicDivider()
                            HowToUseRow(4, stringResource(id = R.string.how_to_use_step_4))
                            BasicDivider()
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun HowToUseRow(num: Int, string: String) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .clickable { }) {
            Column {
                Row(
                    modifier = Modifier
                        .padding(PaddingValues(horizontal = 16.dp, vertical = 8.dp))
                        .heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "$num.",
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        string,
                        style = MaterialTheme.typography.bodyLarge,
                        softWrap = true,
                    )
                }
                Divider(thickness = Dp.Hairline)
            }
        }
    }
}

@Composable
fun BasicDivider() {
    Divider(thickness = Dp.Hairline)
}