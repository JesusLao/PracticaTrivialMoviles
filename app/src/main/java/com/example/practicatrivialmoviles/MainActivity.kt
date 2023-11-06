package com.example.practicatrivialmoviles

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.shapes.Shape
import android.media.Image
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.practicatrivialmoviles.ui.theme.AppTheme
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setBackgroundDrawableResource(R.drawable.ten_in_a_row_);
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
    val opcion_correcta: String,
    var imagen: Int?, // ID de recurso de imagen
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
            /*val mediaPlayer = MediaPlayer.create(LocalContext.current, R.raw.kobosil_avernian)
            mediaPlayer.start()*/
            OnboardingScreen(onContinueClicked = { shouldShowOnboarding = false
                })
        } else {
            val gson = Gson()
            val preguntas: List<Pregunta> = gson.fromJson(
                LocalContext.current.resources.openRawResource(R.raw.preguntas).bufferedReader()
                    .use { it.readText() }, object : TypeToken<List<Pregunta>>() {}.type
            )
            preguntas[32].imagen=R.drawable.q2
            preguntas[33].imagen=R.drawable.image
            preguntas[34].imagen=R.drawable.mapa1


            Quiz(preguntas.shuffled())
        }
    }
}

@Composable
fun OnboardingScreen(onContinueClicked: () -> Unit) {


    Box(modifier = Modifier.fillMaxSize()){
        Image(
            painter = painterResource(R.drawable.inicio_ten_in_a_row),
            contentDescription = "Imagen de la pregunta",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .matchParentSize()
        )
    }





    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 90.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        //Text("Welcome to this Quiz!", style = MaterialTheme.typography.titleLarge)
        Card(
            modifier = Modifier.padding(8.dp), // Añade un margen alrededor del botón
            border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
        ) {
            Button(
                onClick =  onContinueClicked  , colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.inversePrimary,
                    disabledContentColor = MaterialTheme.colorScheme.inversePrimary
                )
            ) {
                Text("Comienza el trivial!", style = MaterialTheme.typography.labelMedium)

            }
        }
    }

}



@Composable
fun OnWinScreen(onContinueClicked: () -> Unit) {

    Box(modifier = Modifier.fillMaxSize()){
        Image(
            painter = painterResource(R.drawable.victoria_ten_in_a_row),
            contentDescription = "Imagen de la pregunta",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .matchParentSize()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 90.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.padding(8.dp), // Añade un margen alrededor del botón
            border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
        ) {
            Button(
                onClick = onContinueClicked, colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.inversePrimary,
                    disabledContentColor = MaterialTheme.colorScheme.inversePrimary
                )
            ) {
                Text("Échate otra", style = MaterialTheme.typography.labelMedium)

            }
        }
    }
}

@Composable
fun OnLoseScreen(onContinueClicked: () -> Unit/*, onReturnToOnboarding: () -> Unit*/) {

    Box(modifier = Modifier.fillMaxSize()){
        Image(
            painter = painterResource(R.drawable.derrota_ten_in_a_row),
            contentDescription = "Imagen de la pregunta",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .matchParentSize()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 90.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.padding(8.dp), // Añade un margen alrededor del botón
            border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
        )
        {
            Button(
                onClick = onContinueClicked, colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.inversePrimary,
                    disabledContentColor = MaterialTheme.colorScheme.inversePrimary
                )
            ) {
                Text("Vuelve a intentarlo", style = MaterialTheme.typography.labelMedium)

            }
        }
    }
}

@Composable
fun Quiz(preguntas: List<Pregunta>) {
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableIntStateOf(-1) }
    var quizCompleted by remember { mutableStateOf(false) }
    var quizFailed by remember { mutableStateOf(false) }



    fun nextQuestion() {
        if (selectedAnswer >= 0) {
            if (preguntas[currentQuestionIndex].opciones[selectedAnswer] == preguntas[currentQuestionIndex].opcion_correcta) {
                if (currentQuestionIndex < 10) {
                    currentQuestionIndex++
                    selectedAnswer = -1
                } else {
                    // Quiz completado
                    quizCompleted = true

                }
            } else {
                // Respuesta incorrecta, ir a la pantalla de pérdida
                quizFailed = true

            }
        }
    }

    if (quizCompleted) {
        OnWinScreen(onContinueClicked = {
            currentQuestionIndex = 0
            selectedAnswer = -1
            quizCompleted = false
            quizFailed = false
        })
    } else if (quizFailed) {
        OnLoseScreen(onContinueClicked = {
            currentQuestionIndex = 0
            selectedAnswer = -1
            quizCompleted = false
            quizFailed = false
        }/*,onReturnToOnboarding = {
            // Esta parte llevará al usuario de regreso a OnboardingScreen
            currentQuestionIndex = 0
            selectedAnswer = -1
            quizCompleted = false
            quizFailed = false

        }*/)
    }else {
        Box(modifier = Modifier.fillMaxSize()){
            Image(
                painter = painterResource(R.drawable.pantalla_pregunta),
                contentDescription = "Imagen de la pregunta",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .matchParentSize()
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = preguntas[currentQuestionIndex].pregunta,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 24.sp,
                modifier = Modifier.padding(horizontal = 70.dp)
            )

            preguntas[currentQuestionIndex].imagen?.let { imagenId ->
                Image(
                    painter = painterResource(imagenId),
                    contentDescription = "Imagen de la pregunta",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                )
            }

            //preguntas[currentQuestionIndex].videoUrl?.let { videoUrl ->
                /*val exoPlayer=ExoPlayer.Builder(LocalContext.current).build()
                val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
                exoPlayer.setMediaItem(mediaItem)

                val playerView = StyledPlayerView(LocalContext.current)
                playerView.player=exoPlayer
                
                DisposableEffect(AndroidView(factory = {playerView})){
                    exoPlayer.prepare()
                    exoPlayer.playWhenReady=true

                    onDispose { exoPlayer.release() }
                }*/



                /*AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            settings.javaScriptEnabled = true
                        }
                    },
                    update = { webView ->
                        webView.loadUrl(videoUrl)
                    }
                )*/
            //}

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
                    nextQuestion()


                },
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.inversePrimary,
                    disabledContentColor = MaterialTheme.colorScheme.inversePrimary
                ),
            ) {
                Text(text = "Siguiente pregunta", style = MaterialTheme.typography.labelMedium)
            }
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