package com.example.workshop4

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.example.workshop4.ui.theme.Workshop4Theme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

val Context.dataStore by preferencesDataStore("settings")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
        val preferences = getSharedPreferences("mysettings", MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Workshop4Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(horizontal = 10.dp)
                    ) {
                        Text("Shared Preferences", fontSize = 30.sp)
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            var message by remember { mutableStateOf("") }
                            TextField(
                                message,
                                onValueChange = {
                                    message = it
                                })
                            Spacer(modifier = Modifier.weight(1.0f))
                            Column {
                                Button(onClick = {
                                    message = preferences.getString("message", "") ?: ""
                                }) {
                                    Text("Load")
                                }
                                Button(onClick = {
                                    preferences.edit().putString("message", message).apply()
                                }) {
                                    Text("Save")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(100.dp))
                        Text("Preferences DataStore", fontSize = 30.sp)
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            var message by remember { mutableStateOf("") }
                            TextField(
                                message,
                                onValueChange = {
                                    message = it
                                })
                            Spacer(modifier = Modifier.weight(1.0f))
                            Column {
                                Button(onClick = {
                                    message = loadPreference(stringPreferencesKey("message")) ?: ""
                                }) {
                                    Text("Load")
                                }
                                Button(onClick = {
                                    savePreference(stringPreferencesKey("message"), message)
                                }) {
                                    Text("Save")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(100.dp))
                        Text("Listen Preference", fontSize = 30.sp)
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            var message by remember { mutableStateOf("") }
                            TextField(message, onValueChange = {})
                            Spacer(modifier = Modifier.weight(1.0f))
                            Button(onClick = {
                                listenPreference(stringPreferencesKey("message")) {
                                    message = it.toString()
                                }
                            }) {
                                Text("Listen")
                            }
                        }
                        Spacer(modifier = Modifier.height(100.dp))
                        var showPermissionDialog by remember { mutableStateOf(false) }
                        if (showPermissionDialog) {
                            AlertDialog(
                                icon = { Icon(Icons.Default.Info, contentDescription = "") },
                                title = { Text(text = "Please give permission") },
                                text = { Text("We need your permission to access your location") },
                                onDismissRequest = { showPermissionDialog = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        showPermissionDialog = false
                                        requestPermissionLauncher.launch(ACCESS_COARSE_LOCATION)
                                    }) { Text("Maybe") }
                                },
                                dismissButton = {
                                    TextButton(onClick = {
                                        showPermissionDialog = false
                                    }) { Text("Never") }
                                })
                        }
                        Button(onClick = {
                            when {
                                ContextCompat.checkSelfPermission(this@MainActivity, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED -> {
                                    Toast.makeText(this@MainActivity, "Permission already granted", Toast.LENGTH_SHORT).show()
                                }

                                ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, ACCESS_COARSE_LOCATION) -> {
                                    showPermissionDialog = true
                                }

                                else -> {
                                    requestPermissionLauncher.launch(ACCESS_COARSE_LOCATION)
                                }
                            }
                        }) {
                            Text("Permission")
                        }
                    }
                }
            }
        }
    }

    private fun <T> loadPreference(key: Preferences.Key<T>): T? = runBlocking {
        return@runBlocking dataStore.data.map {
            it[key]
        }.first()
    }

    private fun <T> savePreference(key: Preferences.Key<T>, value: T) = runBlocking {
        dataStore.edit {
            it[key] = value
        }
    }

    private fun <T> listenPreference(key: Preferences.Key<T>, listener: (T?) -> Unit) {
        lifecycleScope.launch {
            dataStore.data.map {
                it[key]
            }.collect {
                listener(it)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Workshop4Theme {
        Greeting("Android")
    }
}