package com.example.calculadorapersonal.logic

import java.text.SimpleDateFormat
import java.util.*

enum class TipoTransaccion { INGRESO, GASTO }

data class Transaccion(
    val id: String = "",  // ID de Firestore
    val monto: Double = 0.0,
    val descripcion: String = "",
    val tipo: TipoTransaccion = TipoTransaccion.GASTO,
    val fecha: String = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
)

fun validarTransaccion(montoTexto: String, descripcion: String): String? {
    val monto = montoTexto.toDoubleOrNull()
    return when {
        monto == null || monto <= 0 -> "Ingresa un monto válido mayor a 0"
        descripcion.isBlank() -> "Ingresa una descripción"
        else -> null
    }
}
