package com.example.cdkbudgetapp.data

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val description: String,
    val amount: Double,
    val category: String,
    val date: String,      // Format: YYYY-MM-DD
    val startTime: String, // Format: HH:mm
    val endTime: String,   // Format: HH:mm
    val photoUri: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
