package com.example.samplelocationpermissionapp

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.samplelocationpermissionapp.ui.theme.SampleLocationPermissionAppTheme
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SampleLocationPermissionAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LocationScreen(
                        innerPadding = innerPadding
                    )
                }
            }
        }
    }
}

@Composable
fun LocationScreen(
    innerPadding: PaddingValues
) {
    val context = LocalContext.current
    var location by remember { mutableStateOf("") }

    /**
     * Instanciando um lançador
     *
     * rememberLauncherForActivityResult: é uma função que cria um lançador de resultados de atividade
     * que pode ser usado para solicitar permissões ou iniciar outras atividades que retornam um resultado
     */
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = {}
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                if (location.isBlank().not()) return@Button

                if (hasLocationPermission(context)) {
                    Toast.makeText(context, "Permissão já concedida", Toast.LENGTH_SHORT).show()

                    getCurrentLocation(context) { lat, long ->
                        location = "Latitude: $lat, Longitude: $long"
                    }
                } else {
                    Toast.makeText(context, "Permissão não concedida", Toast.LENGTH_SHORT).show()

                    /**
                     * Exibe um dialog/prompt para o usuário conceder a permissão
                     */
                    requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        ) {
            Text(text = "Obter minha localização atual")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            text = "Sua localização é:",
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            text = location,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Verifica se a permissão do tipo ACCESS_FINE_LOCATION é igual a PERMISSION_GRANTED,
 * ou seja, se ela foi concedida.
 */
private fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

/**
 * Função que obtém a localização atual do usuário
 */
private fun getCurrentLocation(context: Context, callback: (Double, Double) -> Unit) {
    /**
     *  * A instância location armazena a localização atual do usuário, que é obtida por meio do LocationServices
     *  * O FusedLocationProviderClient é uma API do Google Play Services que fornece a localização do dispositivo.
     */
    val location = LocationServices.getFusedLocationProviderClient(context)

    /**
     * Verifica se a permissão do tipo ACCESS_FINE_LOCATION é igual a PERMISSION_GRANTED,
     * ou seja, se ela foi concedida.
     */
    if (ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        location.lastLocation
            .addOnSuccessListener {
                /**
                 * Caso sucesso, retorna a latitude e longitude no callback.
                 */
                if (it != null) {
                    val lat = it.latitude
                    val long = it.longitude
                    callback(lat, long)
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }
}