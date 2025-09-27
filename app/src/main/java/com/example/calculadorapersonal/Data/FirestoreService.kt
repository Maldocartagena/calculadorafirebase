package com.example.calculadorapersonal.data

import com.example.calculadorapersonal.logic.Transaccion
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object FirestoreService {
    private val db = FirebaseFirestore.getInstance()
    private val coleccion = db.collection("transacciones")

    // Guardar transacción y asignar automáticamente ID de Firestore
    suspend fun agregarTransaccion(transaccion: Transaccion) {
        val docRef = coleccion.document() // Genera ID único
        val transConId = transaccion.copy(id = docRef.id)
        docRef.set(transConId).await()
    }

    // Obtener todas las transacciones en tiempo real como Flow
    fun obtenerTransacciones(): Flow<List<Transaccion>> = callbackFlow {
        val listener = coleccion
            .orderBy("fecha")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val lista = snapshot?.documents
                    ?.mapNotNull { it.toObject(Transaccion::class.java) }
                    ?: emptyList()
                trySend(lista)
            }
        awaitClose { listener.remove() }
    }

    // Eliminar transacción por ID
    suspend fun eliminarTransaccion(id: String) {
        coleccion.document(id).delete().await()
    }
}


