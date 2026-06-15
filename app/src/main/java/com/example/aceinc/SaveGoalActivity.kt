package com.example.aceinc

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class SaveGoalActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var firestore: FirebaseFirestore

    private var userId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_goal)

        db = DatabaseHelper(this)
        firestore = FirebaseFirestore.getInstance()

        userId = intent.getIntExtra("userId", -1)

        val minGoalInput =
            findViewById<EditText>(R.id.minGoalInput)

        val maxGoalInput =
            findViewById<EditText>(R.id.maxGoalInput)

        val saveBtn =
            findViewById<Button>(R.id.saveGoalBtn)

        saveBtn.setOnClickListener {

            val minGoal =
                minGoalInput.text.toString()
                    .toDoubleOrNull()

            val maxGoal =
                maxGoalInput.text.toString()
                    .toDoubleOrNull()

            if (minGoal == null || maxGoal == null) {

                Toast.makeText(
                    this,
                    "Enter valid values",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val success =
                db.saveBudgetGoal(
                    userId,
                    minGoal,
                    maxGoal
                )
            android.util.Log.d(
                "GOAL_SAVE",
                "userId=$userId min=$minGoal max=$maxGoal success=$success"
            )

            if (success) {

                val goalData = hashMapOf(
                    "userId" to userId,
                    "minGoal" to minGoal,
                    "maxGoal" to maxGoal,
                    "updatedAt" to System.currentTimeMillis()
                )

                firestore.collection("budget_goals")
                    .document(userId.toString())
                    .set(goalData)

                Toast.makeText(
                    this,
                    "Goal Saved ✅",
                    Toast.LENGTH_SHORT
                ).show()

                finish()

            } else {

                Toast.makeText(
                    this,
                    "Error Saving Goal",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}