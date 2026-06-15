package com.example.aceinc

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LandingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        val startBtn = findViewById<Button>(R.id.startBtn)

        startBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        addButtonEffect(startBtn)
    }

    private fun addButtonEffect(button: Button) {

        button.setOnTouchListener { v, event ->

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    v.startAnimation(
                        AnimationUtils.loadAnimation(
                            this,
                            R.anim.button_press
                        )
                    )
                }

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    v.clearAnimation()
                }
            }

            false
        }
    }
}