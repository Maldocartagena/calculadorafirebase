package com.example.calculadorapersonal.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calculadorapersonal.logic.Transaccion
import com.example.calculadorapersonal.data.FirestoreService
import com.example.calculadorapersonal.logic.Item
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HistorialScreen() {
    var transacciones by remember { mutableStateOf<List<Transaccion>>(emptyList()) }

    LaunchedEffect(Unit) {
        FirestoreService.obtenerTransacciones().collectLatest { lista ->
            transacciones = lista.reversed() // Mostrar últimas primero
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (transacciones.isEmpty()) {
            item {
                Text("No hay transacciones aún")
            }
        } else {
            items(transacciones) { trans ->
                Item(trans)
            }
        }
    }
}
