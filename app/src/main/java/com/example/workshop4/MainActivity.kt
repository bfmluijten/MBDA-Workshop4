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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.workshop4.ui.theme.Workshop4Theme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
    }

    fun<T> loadPreference(key: Preferences.Key<T>): T? = runBlocking {
        return@runBlocking dataStore.data.map {
            it[key]
        }.first()
    }

    fun<T> savePreference(key: Preferences.Key<T>, value: T) = runBlocking {
        dataStore.edit {
            it[key] = value
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val preferences = getSharedPreferences("mysettings", MODE_PRIVATE)
        var message by mutableStateOf("")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Workshop4Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Row(
                        modifier = Modifier.padding(innerPadding),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(message,
                            onValueChange = {
                                message = it
                            })
                        Spacer(modifier = Modifier.weight(1.0f))
                        Column {
                            Button(onClick = {
//                                message = preferences.getString("message", "") ?: ""
                                message = loadPreference(stringPreferencesKey("message")) ?: ""
                            }) {
                                Text("Load")
                            }
                            Button(onClick = {
//                                preferences.edit().putString("message", message).apply()
                                savePreference(stringPreferencesKey("message"), message)
                            }) {
                                Text("Save")
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