package com.example.aceinc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        userId = intent.getIntExtra("userId", -1)

        findViewById<Button>(R.id.backBtn).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.accountBtn).setOnClickListener {

            val intent =
                Intent(this, AccountActivity::class.java)

            intent.putExtra("userId", userId)

            startActivity(intent)
        }

        findViewById<Button>(R.id.budgetBtn).setOnClickListener {

            val intent =
                Intent(this, DashboardActivity::class.java)

            intent.putExtra("userId", userId)

            startActivity(intent)
        }

        findViewById<Button>(R.id.gameBtn).setOnClickListener {

            val intent =
                Intent(this, BudgetBossGame::class.java)

            intent.putExtra("userId", userId)

            startActivity(intent)
        }
        findViewById<Button>(R.id.gameBtn)
            .setOnClickListener {

                val intent =
                    Intent(this, BudgetBossGame::class.java)

                intent.putExtra(
                    "userId",
                    intent.getIntExtra("userId",-1)
                )

                startActivity(intent)
            }

        findViewById<Button>(R.id.rewardsBtn)
            .setOnClickListener {

                val intent =
                    Intent(this, RewardsActivity::class.java)

                intent.putExtra(
                    "userId",
                    intent.getIntExtra("userId",-1)
                )

                startActivity(intent)
            }
    }
}