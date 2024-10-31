package com.appsnica.clienteapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.appsnica.clienteapi.ui.theme.ClienteApiTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClienteApiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaConsumoSimple(
                        modifier = Modifier.padding(innerPadding)
                    )
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
//Estructura de la respuesta
data class Personaje(val id: Int, val name: String, val image: String, val url: String)

//Conversión de StringJSON a Lista de Personajes
fun parseJson(jsonResponse: String): List<Personaje> {
    val gson = Gson()
    val listType = object : TypeToken<List<Personaje>>() {}.type
    return gson.fromJson(jsonResponse, listType)
}




@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    ClienteApiTheme {
        Greeting("Android")

    }
}






@Composable
fun PantallaConsumoSimple(modifier: Modifier = Modifier) {
    var personajes by remember { mutableStateOf<List<Personaje>>(emptyList()) }

    LaunchedEffect(Unit) {
        //Consumo de la Api y manejo de la respuesta
        personajes = fetchPersonajes()
    }

    Card {
        Text(
            text = "Total personajes: ${personajes.count()}",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(10.dp)
        )
        LazyColumn {

            items(personajes) { item ->
                Card() {
                    Text(text = item.name, style= MaterialTheme.typography.headlineLarge)
                    Divider(
                        color = Color.White
                        , thickness = 1.dp
                        , modifier = Modifier.padding(2.dp)
                    )
                    Image(
                        painter = rememberAsyncImagePainter(model = item.image),
                        contentDescription = "Character Image",
                        modifier = Modifier
                            .height(150.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )


                }

            }
        }
    }
}

suspend fun fetchPersonajes(): List<Personaje> {
    return withContext(Dispatchers.IO) {
       // EndPoint a consumir
        val url = URL("https://bobsburgers-api.herokuapp.com/characters/")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connect()

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            parseJson(response)
        } else {
            emptyList()
        }
    }
}

























