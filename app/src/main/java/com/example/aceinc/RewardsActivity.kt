package com.example.aceinc

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RewardsActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private var userId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewards)

        db = DatabaseHelper(this)

        userId = intent.getIntExtra("userId", -1)

        val pointsText =
            findViewById<TextView>(R.id.pointsText)

        val badgeText =
            findViewById<TextView>(R.id.badgeText)

        val points =
            db.getBudgetPoints(userId)

        pointsText.text =
            "Budget Points: $points"

        badgeText.text =
            when {
                points >= 10 ->
                    "🥇 Gold Saver"

                points >= 5 ->
                    "🥈 Silver Saver"

                points >= 1 ->
                    "🥉 Bronze Saver"

                else ->
                    "No Badge Yet"
            }
    }
}