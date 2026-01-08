package com.caixaapp.repository

import com.caixaapp.model.Transaction

interface TransactionRepository {
    suspend fun add(transaction: Transaction)
    suspend fun getAll(): List<Transaction>
}
