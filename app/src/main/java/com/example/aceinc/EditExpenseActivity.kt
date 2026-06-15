package com.example.aceinc

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditExpenseActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    private var expenseId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_edit_expense)

        db = DatabaseHelper(this)

        val titleInput = findViewById<EditText>(R.id.editTitle)
        val amountInput = findViewById<EditText>(R.id.editAmount)
        val categoryInput = findViewById<EditText>(R.id.editCategory)

        val updateBtn = findViewById<Button>(R.id.updateBtn)
        val backBtn = findViewById<Button>(R.id.backBtn)

        // Receive expense data
        expenseId = intent.getIntExtra("expenseId", -1)

        val title = intent.getStringExtra("title") ?: ""
        val amount = intent.getDoubleExtra("amount", 0.0)
        val category = intent.getStringExtra("category") ?: ""

        // Populate fields
        titleInput.setText(title)
        amountInput.setText(amount.toString())
        categoryInput.setText(category)

        // Back button
        backBtn.setOnClickListener {
            finish()
        }

        // Update button
        updateBtn.setOnClickListener {

            val newTitle = titleInput.text.toString().trim()
            val newAmountText = amountInput.text.toString().trim()
            val newCategory = categoryInput.text.toString().trim()

            if (newTitle.isEmpty() || newAmountText.isEmpty() || newCategory.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newAmount = newAmountText.toDoubleOrNull()

            if (newAmount == null) {
                Toast.makeText(this, "Enter valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //  (matches new function)
            val success = db.updateExpenseBasic(
                expenseId,
                newTitle,
                newAmount,
                newCategory
            )

            if (success) {
                Toast.makeText(this, "Expense Updated ✅", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}