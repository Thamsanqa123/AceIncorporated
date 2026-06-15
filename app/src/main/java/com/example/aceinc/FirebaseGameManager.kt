package com.example.aceinc

import com.google.firebase.firestore.FirebaseFirestore

class FirebaseGameManager {

    private val db =
        FirebaseFirestore.getInstance()

    fun saveGameStats(stats: GameStats) {

        db.collection("game_stats")
            .document(stats.userId.toString())
            .set(stats)
    }

    fun loadGameStats(
        userId: Int,
        callback: (GameStats?) -> Unit
    ) {

        db.collection("game_stats")
            .document(userId.toString())
            .get()
            .addOnSuccessListener {

                callback(
                    it.toObject(GameStats::class.java)
                )
            }
            .addOnFailureListener {

                callback(null)
            }
    }
}