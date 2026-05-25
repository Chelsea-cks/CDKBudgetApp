package com.example.cdkbudgetapp.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.AndroidViewModel
import com.example.cdkbudgetapp.data.AppDatabase
import com.example.cdkbudgetapp.data.Transaction
import com.example.cdkbudgetapp.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

class TransactionViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.getDatabase(app).transactionDao()
    private val repo = TransactionRepository(dao)
    val transactions = repo.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    // Goals (Monthly)
    val minGoal = MutableStateFlow(1000.0)
    val maxGoal = MutableStateFlow(5000.0)

    val streak = transactions.map { list ->
        calculateStreak(list)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    fun updateGoals(min: Double, max: Double) {
        minGoal.value = min
        maxGoal.value = max
    }

    fun add(
        description: String, 
        amount: Double, 
        category: String, 
        date: String, 
        photoUri: String? = null
    ) {
        viewModelScope.launch {
            repo.insert(
                Transaction(
                    description = description, 
                    amount = amount, 
                    category = category,
                    date = date,
                    photoUri = photoUri
                )
            )
        }
    }

    fun delete(transaction: Transaction) {
        viewModelScope.launch {
            repo.delete(transaction)
        }
    }

    fun getFilteredTransactions(period: String, allTransactions: List<Transaction>): List<Transaction> {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        
        return when (period) {
            "Today" -> {
                allTransactions.filter { isSameDay(it.timestamp, now) }
            }
            "This Week" -> {
                calendar.timeInMillis = now
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                val startOfWeek = calendar.timeInMillis
                allTransactions.filter { it.timestamp >= startOfWeek }
            }
            "This Month" -> {
                calendar.timeInMillis = now
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                val startOfMonth = calendar.timeInMillis
                allTransactions.filter { it.timestamp >= startOfMonth }
            }
            else -> allTransactions
        }
    }

    private fun isSameDay(t1: Long, t2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = t1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = t2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun calculateStreak(transactions: List<Transaction>): Int {
        if (transactions.isEmpty()) return 0
        
        val sortedDates = transactions
            .map { 
                val cal = Calendar.getInstance()
                cal.timeInMillis = it.timestamp
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }
            .distinct()
            .sortedDescending()

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        if (sortedDates.isEmpty()) return 0
        
        val lastDate = sortedDates[0]
        val diff = today - lastDate
        val daysDiff = TimeUnit.MILLISECONDS.toDays(diff)
        
        if (daysDiff > 1) return 0 

        var currentStreak = 1
        for (i in 0 until sortedDates.size - 1) {
            val date1 = sortedDates[i]
            val date2 = sortedDates[i + 1]
            val difference = date1 - date2
            val dayCount = TimeUnit.MILLISECONDS.toDays(difference)
            
            if (dayCount == 1L) {
                currentStreak++
            } else {
                break
            }
        }
        return currentStreak
    }
}
