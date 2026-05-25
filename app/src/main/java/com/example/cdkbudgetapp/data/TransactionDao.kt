package com.example.cdkbudgetapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
@Dao
interface  TransactionDao{
    @Insert
    suspend fun insert(transaction: Transaction)
    @Delete
    suspend fun delete(transaction: Transaction)
    @Query("SELECT * FROM transactions ORDER BY id DESC")
    fun getAll(): Flow<List<Transaction>>

}