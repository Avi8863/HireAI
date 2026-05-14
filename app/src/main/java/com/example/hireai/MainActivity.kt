package com.example.hireai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.JsonParser
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import android.widget.Toast
import com.example.hireai.network.ApiClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HireAIScreen()
        }
    }
}

@Composable
fun HireAIScreen() {

    var role by remember {
        mutableStateOf("")
    }

    var experience by remember {
        mutableStateOf("")
    }

    var skills by remember {
        mutableStateOf("")
    }
    var result by remember {
        mutableStateOf("")
    }
    var isLoading by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "HireAI", style = MaterialTheme.typography.headlineMedium
        )
        OutlinedTextField(value = role, onValueChange = {
            role = it
        }, modifier = Modifier.fillMaxWidth(), label = {
            Text("Role")
        })
        OutlinedTextField(value = experience, onValueChange = {
            experience = it
        }, modifier = Modifier.fillMaxWidth(), label = {
            Text("Experience")
        })
        OutlinedTextField(value = skills, onValueChange = {
            skills = it
        }, modifier = Modifier.fillMaxWidth(), label = {
            Text("Skills")
        })
        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    isLoading = true
                    try {

                        val prompt = """
Generate a complete professional resume.

Role: $role
Experience: $experience years
Skills: $skills

Include:

1. Professional Summary
2. Technical Skills
3. Work Experience
4. Projects
5. Education
6. Certifications
7. Achievements

Make it ATS friendly and professional.
""".trimIndent()

                        val requestBody = hashMapOf(
                            "contents" to listOf(
                                hashMapOf(
                                    "parts" to listOf(
                                        hashMapOf(
                                            "text" to prompt
                                        )
                                    )
                                )
                            )
                        )

                        val response = ApiClient.api.generateContent(
                            body = requestBody
                        )

                        if (response.isSuccessful) {

                            val responseText = response.body()?.string()

                            val jsonObject = JsonParser.parseString(responseText).asJsonObject

                            val text = jsonObject.getAsJsonArray("candidates")
                                .get(0).asJsonObject.getAsJsonObject("content")
                                .getAsJsonArray("parts").get(0).asJsonObject.get("text").asString

                            CoroutineScope(Dispatchers.Main).launch {

                                result = text
                            }
                            isLoading = false
                        } else {

                            CoroutineScope(Dispatchers.Main).launch {

                                result = "Error: ${response.code()} \n ${
                                    response.errorBody()?.string()
                                }"
                            }
                            isLoading = false
                        }

                    } catch (e: Exception) {
                        isLoading = false
                        CoroutineScope(Dispatchers.Main).launch {
                            result = e.message.toString()
                        }
                    }
                }
            }, modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate Resume")
        }
        Text(
            text = result
        )
        if (isLoading) {

            Text(
                text = "Generating Resume..."
            )
        }
    }
}