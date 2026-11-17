package com.example.pr10

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.pm.PackageInfoCompat
import com.example.pr10.ui.theme.Pr10Theme
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pr10Theme {
                val context = LocalContext.current
                val info = context.packageManager.getPackageInfo(context.packageName, 0)
                val version = "v${info.versionName} (${PackageInfoCompat.getLongVersionCode(info)})"

                // === ПР 11 — Firebase Remote Config (п. 3.2) ===
                // Стартовое значение — то, что в R.string.title
                var welcomeMessage by rememberSaveable { mutableStateOf(stringResource(R.string.title)) }
                LaunchedEffect(Unit) {
                    val remoteConfig = Firebase.remoteConfig
                    val configSettings = remoteConfigSettings {
                        // В production cache на 1 час, чтобы не молотить сервер
                        minimumFetchIntervalInSeconds = 3600L
                    }
                    remoteConfig.setConfigSettingsAsync(configSettings)
                    // Дефолт, если Firebase недоступен (например, в debug без интернета)
                    remoteConfig.setDefaultsAsync(mapOf("welcome_message" to "Счётчик"))
                    // fetchAndActivate: забрать + применить
                    remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            welcomeMessage = remoteConfig.getString("welcome_message")
                            Log.d("RemoteConfig", "Fetched: $welcomeMessage")
                        } else {
                            // Если fetch упал — берём дефолт (он уже активирован)
                            welcomeMessage = remoteConfig.getString("welcome_message")
                        }
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    CounterScreen(
                        version = version,
                        welcomeMessage = welcomeMessage,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CounterScreen(version: String, welcomeMessage: String, modifier: Modifier = Modifier) {
    var count by rememberSaveable { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ПР 11: заголовок берётся из Remote Config
        Text(
            text = welcomeMessage,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(32.dp))
        Text(
            text = count.toString(),
            fontSize = 96.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(32.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Arrangement.Center) {
            Button(onClick = { count = nextCount(count, -1) }, enabled = count > 0) {
                Text(stringResource(R.string.minus))
            }
            Spacer(Modifier.width(16.dp))
            Button(onClick = { count = nextCount(count, 1) }) {
                Text(stringResource(R.string.plus))
            }
        }
        TextButton(onClick = { count = 0 }, enabled = count > 0) {
            Text(stringResource(R.string.reset))
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = version,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}