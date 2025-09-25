package com.example.calculadorapersonal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.calculadorapersonal.navigation.AppNavigation
import com.example.calculadorapersonal.ui.theme.CalculadorapersonalTheme
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Firebase.firestore
        setContent {
            // Aquí ejecutamos el tema general de la app
            CalculadorapersonalTheme {
                // Ahora usamos la navegación en lugar de llamar directamente a Calculadora
                AppNavigation()
            }
        }
    }
}