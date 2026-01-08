package com.caixaapp.repository

import com.caixaapp.model.Transaction

class FirebaseTransactionRepository(
    private val config: FirebaseConfig
) : TransactionRepository {

    override suspend fun add(transaction: Transaction) {
        if (!config.enabled) {
            return
        }
        // Implementação futura com Firebase Realtime Database.
    }

    override suspend fun getAll(): List<Transaction> {
        if (!config.enabled) {
            return emptyList()
        }
        // Implementação futura com Firebase Realtime Database.
        return emptyList()
    }
}

data class FirebaseConfig(
    val databaseUrl: String,
    val enabled: Boolean
)
