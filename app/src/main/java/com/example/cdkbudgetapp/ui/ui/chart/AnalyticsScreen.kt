package com.example.cdkbudgetapp.ui.ui.chart

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cdkbudgetapp.data.Transaction
import com.example.cdkbudgetapp.viewmodel.TransactionViewModel
import com.github.mikephil.charting.charts.*
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@SuppressLint("AutoboxingStateCreation")
@Composable
fun AnalyticsScreen(
    transactions: List<Transaction>, 
    isDarkTheme: Boolean,
    vm: TransactionViewModel = viewModel()
) {
    var selectedChart by remember { mutableStateOf(0) }
    val chartTypes = listOf("Pie", "Bar", "Line")
    
    var selectedPeriod by remember { mutableStateOf("All Time") }
    val periods = listOf("Today", "This Week", "This Month", "All Time")
    var expanded by remember { mutableStateOf(false) }

    val filteredList = vm.getFilteredTransactions(selectedPeriod, transactions)
    val grouped = filteredList.groupBy { it.category }
    val categoryTotals = grouped.mapValues { it.value.sumOf { t -> t.amount } }
    
    val minGoal by vm.minGoal.collectAsState()
    val maxGoal by vm.maxGoal.collectAsState()

    val textColor = if (isDarkTheme) Color.WHITE else Color.BLACK

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Spacer(modifier = Modifier.height(40.dp))
        Text("Spending Analytics", style = MaterialTheme.typography.headlineMedium)
        
        Spacer(modifier = Modifier.height(8.dp))

        // Period Selector
        Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
            OutlinedButton(onClick = { expanded = true }) {
                Text(selectedPeriod)
                Icon(Icons.Default.ArrowDropDown, "Select Period")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                periods.forEach { period ->
                    DropdownMenuItem(
                        text = { Text(period) },
                        onClick = {
                            selectedPeriod = period
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ScrollableTabRow(
            selectedTabIndex = selectedChart,
            edgePadding = 0.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            chartTypes.forEachIndexed { index, title ->
                Tab(
                    selected = selectedChart == index,
                    onClick = { selectedChart = index },
                    text = { Text(title) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.weight(1f)) {
            if (filteredList.isEmpty()) {
                Text("No data available for this period.")
            } else {
                when (selectedChart) {
                    0 -> RenderPieChart(categoryTotals, textColor)
                    1 -> RenderBarChart(categoryTotals, textColor, false, minGoal, maxGoal)
                    2 -> RenderLineChart(filteredList, textColor, minGoal, maxGoal)
                }
            }
        }
    }
}

@Composable
fun RenderPieChart(data: Map<String, Double>, textColor: Int) {
    val entries = data.map { PieEntry(it.value.toFloat(), it.key) }
    AndroidView(factory = { context ->
        PieChart(context).apply {
            val dataSet = PieDataSet(entries, "").apply {
                colors = getChartColors()
                valueTextColor = Color.rgb(0,0,0)
                valueTextSize = 12f
            }
            this.data = PieData(dataSet)
            this.description.isEnabled = false
            this.legend.textColor = textColor
            this.setHoleColor(Color.TRANSPARENT)
            this.setEntryLabelColor(Color.rgb(0,0,0))
            this.animateY(800)
            this.invalidate()
        }
    }, modifier = Modifier.fillMaxSize())
}

@Composable
fun RenderBarChart(data: Map<String, Double>, textColor: Int, isHorizontal: Boolean, minGoal: Double, maxGoal: Double) {
    val entries = data.entries.mapIndexed { index, entry ->
        BarEntry(index.toFloat(), entry.value.toFloat())
    }
    val categories = data.keys.toList()

    AndroidView(factory = { context ->
        val chart = if (isHorizontal) HorizontalBarChart(context) else BarChart(context)
        chart.apply {
            val dataSet = BarDataSet(entries, "Amount").apply {
                colors = getChartColors()
                valueTextColor = textColor
            }
            this.data = BarData(dataSet)
            this.description.isEnabled = false
            this.legend.textColor = textColor
            
            this.xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(categories)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                this.textColor = textColor
            }
            
            this.axisLeft.apply {
                this.textColor = textColor
                removeAllLimitLines()
                addLimitLine(LimitLine(minGoal.toFloat(), "Min Goal").apply { 
                    lineColor = Color.GREEN
                    this.textColor = textColor
                })
                addLimitLine(LimitLine(maxGoal.toFloat(), "Max Goal").apply { 
                    lineColor = Color.RED
                    this.textColor = textColor
                })
            }
            this.axisRight.isEnabled = false
            
            this.animateY(800)
            this.invalidate()
        }
    }, modifier = Modifier.fillMaxSize())
}

@Composable
fun RenderLineChart(transactions: List<Transaction>, textColor: Int, minGoal: Double, maxGoal: Double) {
    val entries = transactions.mapIndexed { index, transaction ->
        Entry(index.toFloat(), transaction.amount.toFloat())
    }

    AndroidView(factory = { context ->
        LineChart(context).apply {
            val dataSet = LineDataSet(entries, "Transaction History").apply {
                color = Color.BLUE
                setCircleColor(Color.BLUE)
                lineWidth = 2f
                circleRadius = 4f
                setDrawCircleHole(false)
                valueTextColor = textColor
                setDrawFilled(true)
                fillColor = Color.BLUE
                fillAlpha = 50
            }
            this.data = LineData(dataSet)
            this.description.isEnabled = false
            this.legend.textColor = textColor
            this.xAxis.textColor = textColor
            
            this.axisLeft.apply {
                this.textColor = textColor
                removeAllLimitLines()
                addLimitLine(LimitLine(minGoal.toFloat(), "Min").apply { 
                    lineColor = Color.GREEN
                    this.textColor = textColor
                })
                addLimitLine(LimitLine(maxGoal.toFloat(), "Max").apply { 
                    lineColor = Color.RED
                    this.textColor = textColor
                })
            }
            this.axisRight.isEnabled = false
            this.animateX(800)
            this.invalidate()
        }
    }, modifier = Modifier.fillMaxSize())
}

fun getChartColors(): List<Int> {
    return listOf(
        Color.rgb(255, 182, 193),   // soft pink
        Color.rgb(255, 247, 140),   // yellow
        Color.rgb(255, 208, 140),   // peach
        Color.rgb(140, 234, 255),   // sky blue
        Color.rgb(255, 140, 157),   // pink-red
        Color.rgb(217, 80, 138),    // magenta
        Color.rgb(254, 149, 7),     // orange
        Color.rgb(254, 247, 120),   // bright yellow
        Color.rgb(186, 145, 255),   // lavender (replaces muted green)
        Color.rgb(53, 194, 209),    // cyan
        Color.rgb(64, 89, 128),     // navy
        Color.rgb(210, 170, 120)    // warm beige (replaces olive)
    )
}
