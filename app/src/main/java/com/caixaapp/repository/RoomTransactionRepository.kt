package com.caixaapp.repository

import com.caixaapp.model.Transaction
import com.caixaapp.model.TransactionType
import java.time.LocalDate

/**
 * Concrete implementation of the TransactionRepository that uses Room.
 * Its only job is to communicate with the DAO and map data between the
 * Entity (database layer) and Model (domain layer).
 */
class RoomTransactionRepository(
    private val dao: TransactionDao // This now correctly refers to the @Dao interface
) : TransactionRepository {

    override suspend fun add(transaction: Transaction) {
        // Convert the domain model to a database entity before inserting
        dao.insert(transaction.toEntity())
    }

    override suspend fun getAll(): List<Transaction> {
        // Get all entities from the DAO and map them back to the domain model
        return dao.getAll().map { it.toModel() }
    }
}


// --- Helper Functions to map between Model and Entity ---

/**
 * Converts a domain `Transaction` (Model) into a `TransactionEntity` for the database.
 */
private fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        valor = valor,
        descricao = descricao,
        data = data.toString(), // Converts LocalDate to String for storage
        tipo = tipo.name,
        pessoaId = pessoaId
    )
}

/**
 * Converts a database `TransactionEntity` back into a domain `Transaction` (Model).
 */
private fun TransactionEntity.toModel(): Transaction {
    return Transaction(
        id = id,
        valor = valor,
        descricao = descricao,
        data = LocalDate.parse(data), // Converts String from DB back to LocalDate
        tipo = TransactionType.valueOf(tipo),
        pessoaId = pessoaId
    )
}
