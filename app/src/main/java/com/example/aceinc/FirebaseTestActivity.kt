package com.example.aceinc

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = FirebaseFirestore.getInstance()

        val testData = hashMapOf(
            "message" to "Firebase Connected",
            "app" to "AceInc"
        )

        db.collection("test")
            .document("connection")
            .set(testData)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Firebase Connected Successfully",
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Firebase Failed",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}