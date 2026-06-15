package com.example.aceinc

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent

class BudgetBossGame : AppCompatActivity() {

    private lateinit var buttons: Array<Button>
    private lateinit var statusText: TextView

    private lateinit var db: DatabaseHelper
    private lateinit var firestore: FirebaseFirestore

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_boss_game)

        db = DatabaseHelper(this)
        firestore = FirebaseFirestore.getInstance()

        userId = intent.getIntExtra("userId", -1)

        statusText = findViewById(R.id.statusText)

        buttons = arrayOf(
            findViewById(R.id.btn0),
            findViewById(R.id.btn1),
            findViewById(R.id.btn2),
            findViewById(R.id.btn3),
            findViewById(R.id.btn4),
            findViewById(R.id.btn5),
            findViewById(R.id.btn6),
            findViewById(R.id.btn7),
            findViewById(R.id.btn8)
        )

        for (button in buttons) {

            button.setOnClickListener {

                if (button.text.isEmpty()) {

                    button.text = "X"

                    // PLAYER WIN
                    if (checkWinner("X")) {

                        val earnedPoint = db.recordWin(userId)

                        saveGameStatsToFirebase()

                        statusText.text =
                            if (earnedPoint) {
                                "🎉 WIN #${db.getWins(userId)}\n+1 Budget Point Earned!"
                            } else {
                                "You Win!\nWins: ${db.getWins(userId)}"
                            }

                        disableBoard()
                        return@setOnClickListener


                    }
                    val wins = db.getWins(userId)

                    when(wins) {

                        10 -> {
                            db.unlockReward(userId,"Bronze Saver 🥉")
                            saveRewardsToFirebase()
                        }

                        25 -> {
                            db.unlockReward(userId,"Silver Saver 🥈")
                            saveRewardsToFirebase()
                        }

                        50 -> {
                            db.unlockReward(userId,"Gold Saver 🥇")
                            saveRewardsToFirebase()
                        }

                        100 -> {
                            db.unlockReward(userId,"Budget Boss 👑")
                            saveRewardsToFirebase()
                        }
                    }

                    // DRAW CHECK
                    if (isBoardFull()) {

                        statusText.text = "Draw!"

                        disableBoard()
                        return@setOnClickListener
                    }

                    // AI MOVE
                    aiMove()

                    // AI WIN
                    if (checkWinner("O")) {

                        db.recordLoss(userId)

                        saveGameStatsToFirebase()

                        statusText.text = "Budget Boss Wins!"

                        disableBoard()
                        return@setOnClickListener
                    }

                    // DRAW AFTER AI
                    if (isBoardFull()) {

                        statusText.text = "Draw!"

                        disableBoard()
                    }
                }
            }
        }

        findViewById<Button>(R.id.resetBtn)
            .setOnClickListener {
                resetBoard()
            }

        loadStats()

        val rewardsBtn = findViewById<Button?>(R.id.rewardsBtn)

        rewardsBtn?.setOnClickListener {

            val intent = Intent(
                this,
                RewardsActivity::class.java
            )

            intent.putExtra(
                "userId",
                userId
            )

            startActivity(intent)
        }
    }

    private fun saveGameStatsToFirebase() {

        val data = hashMapOf(
            "wins" to db.getWins(userId),
            "losses" to db.getLosses(userId),
            "budgetPoints" to db.getBudgetPoints(userId)
        )

        firestore.collection("game_stats")
            .document(userId.toString())
            .set(data)
    }

    private fun loadStats() {

        statusText.text =
            "Wins: ${db.getWins(userId)} | Points: ${db.getBudgetPoints(userId)}"
    }

    private fun aiMove() {

        for (button in buttons) {

            if (button.text.isEmpty()) {
                button.text = "O"
                break
            }
        }
    }

    private fun checkWinner(symbol: String): Boolean {

        val b = buttons.map { it.text.toString() }

        return (
                (b[0] == symbol && b[1] == symbol && b[2] == symbol) ||
                        (b[3] == symbol && b[4] == symbol && b[5] == symbol) ||
                        (b[6] == symbol && b[7] == symbol && b[8] == symbol) ||
                        (b[0] == symbol && b[3] == symbol && b[6] == symbol) ||
                        (b[1] == symbol && b[4] == symbol && b[7] == symbol) ||
                        (b[2] == symbol && b[5] == symbol && b[8] == symbol) ||
                        (b[0] == symbol && b[4] == symbol && b[8] == symbol) ||
                        (b[2] == symbol && b[4] == symbol && b[6] == symbol)
                )
    }

    private fun isBoardFull(): Boolean {

        for (button in buttons) {

            if (button.text.isEmpty()) {
                return false
            }
        }

        return true
    }

    private fun disableBoard() {

        for (button in buttons) {
            button.isEnabled = false
        }
    }

    private fun resetBoard() {

        for (button in buttons) {

            button.text = ""
            button.isEnabled = true
        }

        statusText.text =
            "Wins: ${db.getWins(userId)} | Points: ${db.getBudgetPoints(userId)}"
    }
    private fun saveRewardsToFirebase() {

        val rewards =
            db.getUnlockedRewards(userId)

        val rewardData = hashMapOf(
            "userId" to userId,
            "bronzeSaver" to rewards.contains("Bronze Saver 🥉"),
            "silverSaver" to rewards.contains("Silver Saver 🥈"),
            "goldSaver" to rewards.contains("Gold Saver 🥇"),
            "budgetBoss" to rewards.contains("Budget Boss 👑"),
            "updatedAt" to System.currentTimeMillis()
        )

        firestore.collection("rewards")
            .document(userId.toString())
            .set(rewardData)
    }
}