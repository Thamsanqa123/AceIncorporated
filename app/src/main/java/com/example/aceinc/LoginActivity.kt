package com.example.aceinc

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = DatabaseHelper(this)

        val usernameInput = findViewById<EditText>(R.id.username)
        val passwordInput = findViewById<EditText>(R.id.password)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val registerBtn = findViewById<Button>(R.id.signupBtn)

        //  LOGIN
        loginBtn.setOnClickListener {

            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = db.loginUser(username, password)

            if (userId != -1) {
                Toast.makeText(this, "Welcome ", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("userId", userId)
                startActivity(intent)
                finish()

            } else {
                Toast.makeText(this, "Invalid login ❌", Toast.LENGTH_SHORT).show()
            }
        }

        //  REGISTER PAGE
        registerBtn.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}