package com.example.practicatrivialmoviles

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.practicatrivialmoviles.ui.theme.AppTheme
import org.json.JSONArray
import org.json.JSONObject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.content.res.Resources

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                MyApp()
            }
        }
    }
}
data class Pregunta(
    val pregunta: String,
    val opciones: List<String>,
    val opcion_correcta: String
)

@Composable
fun MyApp() {
    var arrQuestions = mutableListOf<String>()
    var arrAnswers = mutableListOf<List<String>>()

    /*val context = LocalContext.current // Necesitas obtener el contexto actual.

    val jsonData = context.resources.openRawResource(context.resources.getIdentifier("pre","raw", context.packageName)).bufferedReader().use { it.readText() }
    val outputJsonString=JSONObject(jsonData)*/

    Surface(
        modifier = Modifier.fillMaxSize(),
        tonalElevation = 5.dp,
        color = MaterialTheme.colorScheme.background
    ) {
        var shouldShowOnboarding by rememberSaveable { mutableStateOf(true) }

        if (shouldShowOnboarding) {
            OnboardingScreen(onContinueClicked = { shouldShowOnboarding = false })
        } else {
            /*val questions = outputJsonString.getJSONArray("preguntas") as JSONArray

            for (i in 0 until questions.length()){
                val question = questions.getJSONObject(i).getString("pregunta")
                arrQuestions.add(question)



            }*/
            val gson = Gson()
            //val listType = object : TypeToken<List<List<String>>>() {}.type
            val preguntas: List<Pregunta> = gson.fromJson(LocalContext.current.resources.openRawResource(R.raw.preguntas).bufferedReader().use { it.readText() }, object : TypeToken<List<Pregunta>>() {}.type)


            Quiz(preguntas)
        }
    }
}

fun loadPreguntasFromJson(context: Context, fileName: String): List<Pregunta> {
    val json: String = try {
        val resourceId = context.resources.getIdentifier(fileName, "raw", context.packageName)
        val jsonStream = context.resources.openRawResource(resourceId)
        jsonStream.bufferedReader().use { it.readText() }
    } catch (e: Resources.NotFoundException) {
        ""
    }

    return if (json.isNotEmpty()) {
        val gson = Gson()
        val preguntaList: List<Pregunta> = gson.fromJson(json, Array<Pregunta>::class.java).toList()
        preguntaList
    } else {
        emptyList()
    }
}


/*private fun readJSONFromResource(context: Context, resourceId: Int): String {
    val inputStream: InputStream = context.assets.open("raw/pre.json")
    val reader = BufferedReader(InputStreamReader(inputStream))
    val sb = StringBuilder()
    var line: String? = reader.readLine()
    while (line != null) {
        sb.append(line).append('\n')
        line = reader.readLine()
    }
    reader.close()
    return sb.toString()
}*/

@Composable
fun OnboardingScreen(onContinueClicked: () -> Unit) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to this Quiz!", style = MaterialTheme.typography.titleLarge)
        Button(
            modifier = Modifier.padding(vertical = 24.dp),
            onClick = onContinueClicked
        ) {
            Text("Continue", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun Quiz(preguntas: List<Pregunta>) {
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableIntStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp, vertical = 100.dp)
            .background(MaterialTheme.colorScheme.background,MaterialTheme.shapes.extraLarge),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = preguntas[currentQuestionIndex].pregunta,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp)
        ) {
            itemsIndexed(preguntas[currentQuestionIndex].opciones) { index, answer ->
                AnswerCard(
                    answer = answer,
                    isSelected = index == selectedAnswer,
                    onAnswerSelected = {
                        selectedAnswer = index
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (selectedAnswer >= 0) {
                    if (currentQuestionIndex < preguntas.size - 1) {
                        currentQuestionIndex++
                        selectedAnswer = -1
                    } else {
                        // Handle quiz completion
                    }
                }
            },
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.padding(16.dp), colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.inversePrimary,
                disabledContentColor = MaterialTheme.colorScheme.inversePrimary
            ),
        ) {
            Text(text = "Next Question", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun AnswerCard(answer: String, isSelected: Boolean, onAnswerSelected: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onAnswerSelected() },
        colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = answer,
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            overflow = TextOverflow.Clip
        )
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true,
    name = "DefaultPreviewLight"
)
@Composable
fun GreetingPreview() {
    AppTheme {
        /*val questions = listOf(
            "What is 2 + 2?",
            "What is the capital of France?",
            "What is the largest planet in the solar system?"
        )

        val answers = listOf(
            listOf("3", "4", "5", "6"),
            listOf("London", "Berlin", "Paris", "Madrid"),
            listOf("Earth", "Mars", "Jupiter", "Saturn")
        )

        Quiz(questions, answers)*/
    }
}