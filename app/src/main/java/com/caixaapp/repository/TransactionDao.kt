package com.caixaapp.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object (DAO) for the Transaction entity.
 * This is an interface that Room will implement for us.
 * It MUST be annotated with @Dao.
 */
@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY data DESC")
    suspend fun getAll(): List<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity)
}