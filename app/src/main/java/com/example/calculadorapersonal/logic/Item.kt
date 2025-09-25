package com.example.calculadorapersonal.logic

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.calculadorapersonal.logic.Transaccion

@Composable
fun Item(trans: Transaccion) {
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
