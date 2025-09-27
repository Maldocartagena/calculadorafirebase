package com.example.calculadorapersonal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.calculadorapersonal.logic.Transaccion
import com.example.calculadorapersonal.data.FirestoreService
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(navController: androidx.navigation.NavHostController) {

    // Lista de transacciones obtenidas de Firestore
    var transacciones by remember { mutableStateOf(listOf<Transaccion>()) }

    // Mapa para controlar qué transacciones están seleccionadas con checkbox
    val seleccionMap = remember { mutableStateMapOf<String, Boolean>() }

    // Se lanza al iniciar la pantalla, obtiene las transacciones en tiempo real
    LaunchedEffect(Unit) {
        FirestoreService.obtenerTransacciones().collectLatest { lista ->
            transacciones = lista.reversed() // Mostrar más recientes primero
            // Inicializar el estado de selección de cada transacción si no existe
            lista.forEach { seleccionMap.putIfAbsent(it.id, false) }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // Barra superior con título y flecha para volver a la Calculadora
        TopAppBar(
            title = { Text("Historial") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            }
        )

        // Botón para borrar las transacciones seleccionadas (solo de la UI)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = {
                // Filtrar la lista para mantener solo las no seleccionadas
                transacciones = transacciones.filter { seleccionMap[it.id] != true }
                // Limpiar el estado de selección
                seleccionMap.keys.forEach { seleccionMap[it] = false }
            }) {
                Icon(Icons.Default.Delete, contentDescription = "Borrar seleccionadas", tint = Color.Red)
            }
        }

        // Lista de transacciones con LazyColumn
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {

            // Mostrar mensaje si no hay transacciones
            if (transacciones.isEmpty()) {
                item {
                    Text("No hay transacciones aún", color = Color.Gray)
                }
            } else {
                // Iterar sobre cada transacción
                items(transacciones) { trans ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(Color(0xFFF5F5F5))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Checkbox y detalles de la transacción
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = seleccionMap[trans.id] ?: false,
                                onCheckedChange = { checked ->
                                    seleccionMap[trans.id] = checked // actualizar el estado
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("${trans.tipo} - ${trans.descripcion}") // tipo y descripción
                                Text(trans.fecha, color = Color.Gray)       // fecha
                            }
                        }

                        // Mostrar monto con color según tipo de transacción
                        Text(
                            text = "$${String.format("%.2f", trans.monto)}",
                            color = if (trans.tipo.name == "INGRESO") Color(0xFF4CAF50) else Color(0xFFF44336),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
