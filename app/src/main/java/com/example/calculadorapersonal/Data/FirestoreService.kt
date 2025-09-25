package com.example.calculadorapersonal.data

import com.example.calculadorapersonal.logic.Transaccion
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose


object FirestoreService {
    private val db = FirebaseFirestore.getInstance()
    private val coleccion = db.collection("transacciones")

    suspend fun agregarTransaccion(transaccion: Transaccion) {
        coleccion.add(transaccion).await()
    }

    fun obtenerTransacciones(): Flow<List<Transaccion>> = callbackFlow {
        val listener = coleccion
            .orderBy("fecha")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val lista = snapshot?.documents?.mapNotNull { it.toObject(Transaccion::class.java) } ?: emptyList()
                trySend(lista)
            }
        awaitClose { listener.remove() }
    }
}

