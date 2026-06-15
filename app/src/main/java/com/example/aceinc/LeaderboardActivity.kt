package com.example.aceinc

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var leaderboardText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        leaderboardText =
            findViewById(R.id.leaderboardText)

        FirebaseFirestore.getInstance()
            .collection("game_stats")
            .orderBy("wins")
            .get()
            .addOnSuccessListener { result ->

                var text = "🏆 LEADERBOARD\n\n"

                for (doc in result.documents.reversed()) {

                    text +=
                        "User ${doc.id} - ${doc.getLong("wins")} Wins\n"
                }

                leaderboardText.text = text
            }
    }
}