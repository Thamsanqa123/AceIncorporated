package com.example.aceinc

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var aceText: TextView
    private lateinit var taglineText: TextView
    private lateinit var loadingBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        aceText = findViewById(R.id.aceText)
        taglineText = findViewById(R.id.taglineText)
        loadingBar = findViewById(R.id.loadingBar)

        startGlowAnimation()
        revealLogo()
        animateLoading()
    }

    private fun revealLogo() {

        val text = "ACE"

        var index = 0

        Handler(Looper.getMainLooper()).post(object : Runnable {

            override fun run() {

                if (index <= text.length) {

                    aceText.text = text.substring(0, index)

                    index++

                    Handler(Looper.getMainLooper())
                        .postDelayed(this, 180)

                } else {

                    taglineText.animate()
                        .alpha(1f)
                        .setDuration(1000)
                        .start()
                }
            }
        })
    }

    private fun animateLoading() {

        ObjectAnimator.ofInt(
            loadingBar,
            "progress",
            0,
            100
        ).apply {

            duration = 3500

            start()
        }

        Handler(Looper.getMainLooper()).postDelayed({

            startActivity(
                Intent(
                    this,
                    LandingActivity::class.java
                )
            )

            finish()

        }, 4000)
    }

    private fun startGlowAnimation() {

        val glow = findViewById<android.view.View>(R.id.backgroundGlow)

        glow.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(1800)
            .setInterpolator(
                AccelerateDecelerateInterpolator()
            )
            .withEndAction {

                glow.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(1800)
                    .start()
            }
            .start()
    }
}