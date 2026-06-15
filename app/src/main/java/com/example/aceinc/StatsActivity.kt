package com.example.aceinc

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*

class StatsActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private var userId: Int = -1

    private lateinit var totalSpentTxt: TextView
    private lateinit var expenseCountTxt: TextView
    private lateinit var biggestExpenseTxt: TextView
    private lateinit var categoryBreakdownTxt: TextView

    private lateinit var pieChart: PieChart
    private lateinit var progressChart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        db = DatabaseHelper(this)

        userId = intent.getIntExtra("userId", -1)
        if (userId == -1) finish()

        // TEXTS
        totalSpentTxt = findViewById(R.id.totalSpentTxt)
        expenseCountTxt = findViewById(R.id.expenseCountTxt)
        biggestExpenseTxt = findViewById(R.id.biggestExpenseTxt)
        categoryBreakdownTxt = findViewById(R.id.categoryBreakdownTxt)

        // CHARTS
        pieChart = findViewById(R.id.pieChart)
        progressChart = findViewById(R.id.progressChart)

        loadStatistics()
        setupPieChart()
        loadProgressGraph()
    }

    // =========================
    // TEXT STATS
    // =========================
    private fun loadStatistics() {

        val expenses = db.getExpenses(userId)

        val total = expenses.sumOf { it.amount }
        totalSpentTxt.text = "R %.2f".format(total)

        expenseCountTxt.text = expenses.size.toString()

        val biggest = expenses.maxByOrNull { it.amount }
        biggestExpenseTxt.text =
            biggest?.let { "${it.title} (R%.2f)".format(it.amount) } ?: "None"

        val grouped = expenses.groupBy { it.category }

        categoryBreakdownTxt.text =
            grouped.entries.joinToString("\n") {
                "${it.key}: R %.2f".format(it.value.sumOf { e -> e.amount })
            }.ifEmpty { "No data" }
    }

    // =========================
    // PIE CHART
    // =========================
    private fun setupPieChart() {

        val categoryTotals = db.getCategoryTotals(userId)

        val entries = ArrayList<PieEntry>()
        for ((category, amount) in categoryTotals) {
            entries.add(PieEntry(amount.toFloat(), category))
        }

        val dataSet = PieDataSet(entries, "Expenses").apply {
            colors = listOf(
                Color.parseColor("#B388FF"),
                Color.parseColor("#7C4DFF"),
                Color.parseColor("#651FFF"),
                Color.parseColor("#D1C4E9"),
                Color.parseColor("#9575CD")
            )
            valueTextColor = Color.WHITE
            valueTextSize = 12f
        }

        pieChart.apply {
            data = PieData(dataSet)
            description.isEnabled = false
            centerText = "Expenses"
            setCenterTextColor(Color.WHITE)
            holeRadius = 50f
            animateY(1000)
            invalidate()
        }
    }

    // =========================
    // PROGRESS LINE CHART
    // =========================
    private fun loadProgressGraph() {

        val spent = db.getTotalExpenses(userId)
        val maxGoal = db.getBudgetGoal(userId).second

        val entries = listOf(
            Entry(0f, spent.toFloat()),
            Entry(1f, maxGoal.toFloat())
        )

        val dataSet = LineDataSet(entries, "Budget Progress").apply {
            color = Color.parseColor("#B388FF")
            lineWidth = 2f

            setDrawCircles(true)
            circleRadius = 4f
            setCircleColor(Color.parseColor("#B388FF"))

            setDrawValues(false)

            mode = LineDataSet.Mode.CUBIC_BEZIER

            setDrawFilled(true)
            fillAlpha = 50
            fillColor = Color.parseColor("#B388FF")
        }

        progressChart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false

            axisRight.isEnabled = false

            axisLeft.apply {
                textColor = Color.WHITE
                axisMinimum = 0f
            }

            xAxis.apply {
                textColor = Color.WHITE
                setDrawGridLines(false)
            }

            animateX(900)
            invalidate()
        }
    }
}