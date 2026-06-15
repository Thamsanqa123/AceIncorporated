package com.example.aceinc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AccountActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        db = DatabaseHelper(this)

        val usernameText = findViewById<TextView>(R.id.profileName)
        val emailText = findViewById<TextView>(R.id.profileEmail)

        //  GET USER ID
        userId = intent.getIntExtra("userId", -1)

        if (userId == -1) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        //  GET USER DATA FROM DB
        val user = db.getUser(userId)

        if (user != null) {
            val username = user.first

            usernameText.text = username


            emailText.text = "$username@gmail.com"
        } else {
            Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show()
        }

        //  BACK
        findViewById<Button>(R.id.backBtn).setOnClickListener {
            finish()
        }

        //  LOGOUT
        findViewById<Button>(R.id.logoutBtn).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}