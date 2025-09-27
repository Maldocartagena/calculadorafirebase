package com.example.calculadorapersonal.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculadorapersonal.logic.Transaccion
import com.example.calculadorapersonal.logic.Item
import com.example.calculadorapersonal.data.FirestoreService
import kotlinx.coroutines.launch

@Composable
fun HistorialScreen() {
    var transacciones by remember { mutableStateOf<List<Transaccion>>(emptyList()) }
    var seleccionadas by remember { mutableStateOf(setOf<String>()) } // IDs seleccionadas
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Escuchar cambios de Firestore en tiempo real
    LaunchedEffect(Unit) {
        FirestoreService.obtenerTransacciones().collect { lista ->
            transacciones = lista.reversed() // Mostrar últimas primero
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Encabezado con botón de borrar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Historial de Transacciones", fontSize = 20.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            IconButton(
                onClick = { mostrarConfirmacion = true },
                enabled = seleccionadas.isNotEmpty()
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Borrar seleccionadas")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (transacciones.isEmpty()) {
            Text("No hay transacciones aún")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(transacciones) { trans ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = seleccionadas.contains(trans.id),
                            onCheckedChange = { checked ->
                                seleccionadas = if (checked) {
                                    seleccionadas + trans.id
                                } else {
                                    seleccionadas - trans.id
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Item(trans)
                    }
                }
            }
        }
    }

    // Dialogo de confirmación
    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro que deseas eliminar ${seleccionadas.size} transacción(es)?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        seleccionadas.forEach { id ->
                            try {
                                FirestoreService.eliminarTransaccion(id)
                            } catch (_: Exception) {}
                        }
                        seleccionadas = emptySet()
                        mostrarConfirmacion = false
                    }
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacion = false }) { Text("Cancelar") }
            }
        )
    }
}


