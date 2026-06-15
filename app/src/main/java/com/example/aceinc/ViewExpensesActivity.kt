package com.example.aceinc

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent

class ViewExpensesActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExpenseAdapter

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_expenses)

        db = DatabaseHelper(this)

        userId = intent.getIntExtra("userId", -1)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)


        loadExpenses()
    }
    override fun onResume() {
        super.onResume()
        loadExpenses()
    }

    private fun loadExpenses() {

        val expenses = db.getExpenses(userId)

        adapter = ExpenseAdapter(
            expenses,
            db,

            onDelete = {
                loadExpenses()
            },

            onEdit = { expense ->

                val intent = Intent(
                    this,
                    EditExpenseActivity::class.java
                )

                intent.putExtra("expenseId", expense.id)
                intent.putExtra("title", expense.title)
                intent.putExtra("amount", expense.amount)
                intent.putExtra("category", expense.category)

                startActivity(intent)
            }
        )

        recyclerView.adapter = adapter
    }
}