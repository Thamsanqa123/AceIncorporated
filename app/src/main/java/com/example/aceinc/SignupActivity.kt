package com.example.aceinc

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        db = DatabaseHelper(this)
        firestore = FirebaseFirestore.getInstance()

        val email = findViewById<EditText>(R.id.email)
        val username = findViewById<EditText>(R.id.newUsername)
        val password = findViewById<EditText>(R.id.newPassword)
        val btn = findViewById<Button>(R.id.createAccountBtn)

        btn.setOnClickListener {

            val emailText = email.text.toString().trim()
            val userText = username.text.toString().trim()
            val passText = password.text.toString().trim()

            if (emailText.isEmpty() ||
                userText.isEmpty() ||
                passText.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Fill in all fields",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS
                    .matcher(emailText)
                    .matches()
            ) {
                Toast.makeText(
                    this,
                    "Enter a valid email",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (passText.length < 6) {
                Toast.makeText(
                    this,
                    "Password must be at least 6 characters",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // SAVE TO SQLITE
            val success =
                db.registerUser(
                    userText,
                    emailText,
                    passText
                )

            if (success) {

                // GET USER ID
                val userId =
                    db.loginUser(
                        userText,
                        passText
                    )

                android.util.Log.d("SIGNUP", "User ID = $userId")

                // SAVE TO FIREBASE
                val userData = hashMapOf(
                    "userId" to userId,
                    "username" to userText,
                    "email" to emailText,
                    "password" to passText,
                    "createdAt" to System.currentTimeMillis()
                )
                android.util.Log.d(
                    "FIRESTORE_TEST",
                    "Saving user with SQLite ID = $userId"
                )

                firestore.collection("users")
                    .document(userId.toString())
                    .set(userData)

                Toast.makeText(
                    this,
                    "Account created successfully ✅",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(
                    Intent(
                        this,
                        LoginActivity::class.java
                    )
                )

                finish()

            } else {

                Toast.makeText(
                    this,
                    "Username already exists ❌",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}