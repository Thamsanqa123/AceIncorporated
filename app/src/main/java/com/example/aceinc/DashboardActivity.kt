package com.example.aceinc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.Entry


class DashboardActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private var userId: Int = -1

    private lateinit var balanceText: TextView
    private lateinit var pointsText: TextView
    private lateinit var statusText: TextView

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)



        db = DatabaseHelper(this)


        userId = intent.getIntExtra("userId", -1)

        if (userId == -1) {
            finish() // prevents broken state
            return
        }
        db.ensureGameStats(userId)

        // INIT VIEWS SAFELY
        balanceText = findViewById(R.id.balanceText)
        pointsText = findViewById(R.id.pointsText)


        setupButtons()
        updateUI()
    }

    private fun setupButtons() {

        findViewById<Button>(R.id.addExpenseBtn).setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java).apply {
                putExtra("userId", userId)
            })
        }

        findViewById<Button>(R.id.statsBtn).setOnClickListener {
            startActivity(Intent(this, StatsActivity::class.java).apply {
                putExtra("userId", userId)
            })
        }

        findViewById<Button>(R.id.viewExpensesBtn).setOnClickListener {
            startActivity(Intent(this, ViewExpensesActivity::class.java).apply {
                putExtra("userId", userId)
            })
        }

        findViewById<Button>(R.id.accountBtn).setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java).apply {
                putExtra("userId", userId)
            })
        }

        findViewById<Button>(R.id.goalBtn).setOnClickListener {
            startActivity(Intent(this, SaveGoalActivity::class.java).apply {
                putExtra("userId", userId)
            })
        }

        findViewById<Button>(R.id.backBtn).setOnClickListener {
            finish()
        }
    }

    private fun updateUI() {

        val points = db.getBudgetPoints(userId)

        balanceText.text = "R %.2f".format(db.getTotalExpenses(userId))
        pointsText.text = "Your Points: $points"

        val count = db.getExpenseCount(userId)
        val highest = db.getHighestExpense(userId)
        val lowest = db.getLowestExpense(userId)
        val average = db.getAverageExpense(userId)

        findViewById<TextView>(R.id.analysisText).text = """
        Expenses: $count
        Highest: R %.2f
        Lowest: R %.2f
        Average: R %.2f
    """.trimIndent().format(highest, lowest, average)

        val (min, max) = db.getBudgetGoal(userId)
        val remaining = db.getRemainingBudget(userId)

        findViewById<TextView>(R.id.goalText).text =
            "Goal: R %.2f - R %.2f".format(min, max)

        findViewById<TextView>(R.id.remainingText).text =
            "Remaining: R %.2f".format(remaining)

        findViewById<TextView>(R.id.statusGoalText).text =
            if (remaining >= 0) "You're within budget"
            else "OVER BUDGET ⚠️"
    }

}