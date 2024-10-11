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
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.mateusrodcosta.apps.share2storage.screens.AppNavigation
import com.mateusrodcosta.apps.share2storage.screens.SettingsViewModel

class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel = SettingsViewModel()

    private val getSaveLocationDirIntent =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            if (uri == null) return@registerForActivityResult

            Log.d("settings] getSaveLocationDir] uri", uri.toString())
            Log.d("settings] getSaveLocationDir] uri.path", uri.path.toString())

            settingsViewModel.updateDefaultSaveLocation(uri)
        }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val isAppPreference = intent.action == Intent.ACTION_APPLICATION_PREFERENCES

        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        settingsViewModel.initializeWithContext(applicationContext)
        settingsViewModel.assignSaveLocationDirIntent(getSaveLocationDirIntent)
        settingsViewModel.initPreferences()

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val navController = rememberNavController()

            LaunchedEffect(key1 = Unit) {
                if (isAppPreference) navController.navigate("settings")
            }

            Box(Modifier.safeDrawingPadding()) {
                AppNavigation(navController, settingsViewModel, windowSizeClass)
            }
        }
    }
}
