package com.kerberos.trackingSdk

import android.content.Context
import android.content.res.Configuration
import kotlinx.coroutines.runBlocking
import java.util.Locale
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.adhamkhwaldeh.commonlibrary.BaseActivity
import com.kerberos.trackingSdk.ui.MainScreen
import com.kerberos.trackingSdk.ui.theme.ui.theme.MyApplicationTheme
import com.kerberos.trackingSdk.dataStore.AppPrefsStorage
import org.koin.android.ext.android.inject


class MainActivity : BaseActivity() {

   val appPrefsStorage: AppPrefsStorage by inject()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(updateBaseContextLocale(newBase))
    }

    private fun updateBaseContextLocale(context: Context): Context {
        val language = runBlocking { appPrefsStorage.getLanguage() }
        val locale = if (language == "Spanish") {
            Locale("es")
        } else {
            Locale.ENGLISH
        }
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val theme by appPrefsStorage.getThemeFlow().collectAsState(initial = "Light")

            LaunchedEffect(theme) {
                val mode = when (theme) {
                    "Dark" -> AppCompatDelegate.MODE_NIGHT_YES
                    "Light" -> AppCompatDelegate.MODE_NIGHT_NO
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                AppCompatDelegate.setDefaultNightMode(mode)
            }


            MyApplicationTheme {
                MainScreen()
            }
        }
    }
}

