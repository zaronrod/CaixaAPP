package com.caixaapp.repository

import com.caixaapp.model.Transaction
import com.caixaapp.model.TransactionType
import java.time.LocalDate

class RoomTransactionRepository(
    private val dao: TransactionDao
) : TransactionRepository {

    override suspend fun add(transaction: Transaction) {
        dao.insert(transaction.toEntity())
    }

    override suspend fun getAll(): List<Transaction> {
        return dao.getAll().map { it.toModel() }
    }
}

private fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        valor = valor,
        descricao = descricao,
        data = data.toString(),
        tipo = tipo.name,
        pessoaId = pessoaId
    )
}

private fun TransactionEntity.toModel(): Transaction {
    return Transaction(
        id = id,
        valor = valor,
        descricao = descricao,
        data = LocalDate.parse(data),
        tipo = TransactionType.valueOf(tipo),
        pessoaId = pessoaId
    )
}
