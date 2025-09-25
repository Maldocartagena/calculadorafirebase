package com.example.calculadorapersonal.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.calculadorapersonal.logic.TipoTransaccion
import com.example.calculadorapersonal.logic.Transaccion
import com.example.calculadorapersonal.logic.validarTransaccion
import com.example.calculadorapersonal.data.FirestoreService
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState

@Composable
fun Calculadora(onNavigateToHistorial: () -> Unit = {}) {
    var montoTexto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    // Escuchar cambios de Firestore en tiempo real
    val transacciones by FirestoreService.obtenerTransacciones()
        .collectAsState(initial = emptyList())

    // Calcular el saldo dinámicamente
    val saldoActual = transacciones.sumOf { t ->
        if (t.tipo == TipoTransaccion.INGRESO) t.monto else -t.monto
    }

    // Función para agregar transacción
    fun agregarTransaccion(tipo: TipoTransaccion) {
        val error = validarTransaccion(montoTexto, descripcion)
        if (error != null) {
            mensajeError = error
            return
        }
        val monto = montoTexto.toDouble()
        val nuevaTransaccion = Transaccion(monto, descripcion, tipo)

        scope.launch {
            try {
                FirestoreService.agregarTransaccion(nuevaTransaccion)
            } catch (e: Exception) {
                mensajeError = "Error al guardar en la nube: ${e.message}"
            }
        }

        montoTexto = ""
        descripcion = ""
        mensajeError = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Calculadora de Gastos",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4285F4)
        )

        // Tarjeta de saldo disponible
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Saldo Disponible", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "$${String.format("%.2f", saldoActual)}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (saldoActual >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
            }
        }

        OutlinedTextField(
            value = montoTexto,
            onValueChange = { montoTexto = it; mensajeError = "" },
            label = { Text("Monto") },
            placeholder = { Text("0.00") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it; mensajeError = "" },
            label = { Text("Descripción") },
            placeholder = { Text("¿Qué compraste o recibiste?") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (mensajeError.isNotEmpty()) {
            Text(text = mensajeError, color = Color.Red, fontSize = 14.sp)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { agregarTransaccion(TipoTransaccion.INGRESO) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(8.dp)
            ) { Text("INGRESO", fontWeight = FontWeight.Bold) }

            Button(
                onClick = { agregarTransaccion(TipoTransaccion.GASTO) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                shape = RoundedCornerShape(8.dp)
            ) { Text("GASTO", fontWeight = FontWeight.Bold) }

            Button(
                onClick = {
                    montoTexto = ""
                    descripcion = ""
                    mensajeError = ""
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                shape = RoundedCornerShape(8.dp)
            ) { Text("LIMPIAR", fontWeight = FontWeight.Bold) }
        }

        // Transacciones recientes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Transacciones Recientes", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            TextButton(
                onClick = onNavigateToHistorial,
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF4285F4))
            ) {
                Icon(Icons.Default.List, contentDescription = "Ver historial", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ver Todo")
            }
        }

        // Mostrar solo las 3 más recientes
        transacciones.take(3).forEach { trans ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("${trans.tipo} - ${trans.descripcion}")
                        Text(trans.fecha, fontSize = 12.sp, color = Color.Gray)
                    }
                    Text(
                        text = "$${String.format("%.2f", trans.monto)}",
                        color = if (trans.tipo == TipoTransaccion.INGRESO) Color(0xFF4CAF50) else Color(0xFFF44336),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

