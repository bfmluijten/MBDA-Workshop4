package com.example.workshop4

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
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

class MainActivity : ComponentActivity() {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
    }

    fun <T> loadPreference(key: Preferences.Key<T>): T? = runBlocking {
        return@runBlocking dataStore.data.map {
            it[key]
        }.first()
    }

    fun <T> savePreference(key: Preferences.Key<T>, value: T) = runBlocking {
        dataStore.edit {
            it[key] = value
        }
    }

    fun <T> subscribePreference(key: Preferences.Key<T>, callback: (T?) -> Unit) {
        lifecycleScope.launch {
            dataStore.data.map {
                it[key]
            }.collect {
                callback(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val preferences = getSharedPreferences("mysettings", MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Workshop4Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            var message by remember { mutableStateOf("") }
                            TextField(message,
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            var message by remember { mutableStateOf("") }
                            TextField(message,
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            var message by remember { mutableStateOf("") }
                            TextField(message, onValueChange = {})
                            Spacer(modifier = Modifier.weight(1.0f))
                            Button(onClick = {
                                subscribePreference(stringPreferencesKey("message")) {
                                    message = it.toString()
                                }
                            }) {
                                Text("Subscribe")
                            }
                        }
                    }
                }
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