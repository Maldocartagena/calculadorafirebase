package com.example.calculadorapersonal.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.calculadorapersonal.logic.*


object HistorialTransacciones {
    val lista: SnapshotStateList<Transaccion> = mutableStateListOf()

    fun agregar(transaccion: Transaccion) {
        lista.add(transaccion)
    }

    fun limpiar() {
        lista.clear()
    }
}
