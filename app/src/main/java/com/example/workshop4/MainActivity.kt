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
import com.example.workshop4.ui.theme.Workshop4Theme
import kotlinx.coroutines.runBlocking
import java.util.prefs.Preferences

class MainActivity : ComponentActivity() {
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
                }
            }
        }
    }

    fun saveBooleanPreference(key: Preferences.Key<Boolean>, value: Boolean) = runBlocking {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
        return@runBlocking dataStore.edit { it[key] = value }
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