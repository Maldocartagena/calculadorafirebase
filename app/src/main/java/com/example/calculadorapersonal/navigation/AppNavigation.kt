package com.example.calculadorapersonal.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calculadorapersonal.ui.Calculadora
import com.example.calculadorapersonal.ui.HistorialScreen

object Destinos {
    const val CALCULADORA = "calculadora"
    const val HISTORIAL = "historial"
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Destinos.CALCULADORA) {
        composable(Destinos.CALCULADORA) {
            Calculadora(
                onNavigateToHistorial = { navController.navigate(Destinos.HISTORIAL) }
            )
        }
        composable(Destinos.HISTORIAL) {
            HistorialScreen()
        }
    }
}
