package com.example.cdkbudgetapp.repository

import com.example.cdkbudgetapp.data.TransactionDao
import com.example.cdkbudgetapp.data.Transaction
class TransactionRepository(private val dao: TransactionDao){
    val allTransactions = dao.getAll()
    suspend fun insert(transaction: Transaction) = dao.insert(transaction)
    suspend fun delete(transaction: Transaction) = dao.delete(transaction)
}