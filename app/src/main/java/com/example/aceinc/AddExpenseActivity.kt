package com.example.aceinc


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var firestore: FirebaseFirestore

    private val IMAGE_REQUEST_CODE = 100

    private var selectedDate = ""
    private var startTime = ""
    private var endTime = ""
    private var imageUriString = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        db = DatabaseHelper(this)
        firestore = FirebaseFirestore.getInstance()

        val userId = intent.getIntExtra("userId", -1)

        val titleInput = findViewById<EditText>(R.id.titleInput)
        val amountInput = findViewById<EditText>(R.id.amountInput)
        val categoryInput = findViewById<EditText>(R.id.categoryInput)
        val saveBtn = findViewById<Button>(R.id.saveBtn)

        val dateBtn = findViewById<Button>(R.id.dateBtn)
        val startTimeBtn = findViewById<Button>(R.id.startTimeBtn)
        val endTimeBtn = findViewById<Button>(R.id.endTimeBtn)
        val uploadBtn = findViewById<Button>(R.id.uploadImageBtn)
        val previewImage = findViewById<ImageView>(R.id.previewImage)

        // DATE PICKER
        dateBtn.setOnClickListener {

            val calendar = Calendar.getInstance()

            DatePickerDialog(
                this,
                { _, year, month, day ->

                    selectedDate = "$day/${month + 1}/$year"
                    dateBtn.text = selectedDate

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // START TIME
        startTimeBtn.setOnClickListener {

            val calendar = Calendar.getInstance()

            TimePickerDialog(
                this,
                { _, hour, minute ->

                    startTime = "$hour:$minute"
                    startTimeBtn.text = startTime

                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        // END TIME
        endTimeBtn.setOnClickListener {

            val calendar = Calendar.getInstance()

            TimePickerDialog(
                this,
                { _, hour, minute ->

                    endTime = "$hour:$minute"
                    endTimeBtn.text = endTime

                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        // IMAGE PICKER
        uploadBtn.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"

            startActivityForResult(
                intent,
                IMAGE_REQUEST_CODE
            )
        }

        // SAVE EXPENSE
        saveBtn.setOnClickListener {

            val title =
                titleInput.text.toString().trim()

            val amountText =
                amountInput.text.toString().trim()

            val category =
                categoryInput.text.toString().trim()

            if (
                title.isEmpty() ||
                amountText.isEmpty() ||
                category.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Fill all fields",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val amount =
                amountText.toDoubleOrNull()

            if (amount == null) {

                Toast.makeText(
                    this,
                    "Enter valid amount",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (
                selectedDate.isEmpty() ||
                startTime.isEmpty() ||
                endTime.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Select date & time",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val success = db.insertExpense(
                userId,
                title,
                amount,
                category,
                selectedDate,
                startTime,
                endTime,
                imageUriString
            )

            if (success) {

                // FIREBASE SAVE
                val expenseData =
                    hashMapOf(
                        "userId" to userId,
                        "title" to title,
                        "amount" to amount,
                        "category" to category,
                        "date" to selectedDate,
                        "startTime" to startTime,
                        "endTime" to endTime,
                        "imageUri" to imageUriString,
                        "timestamp" to System.currentTimeMillis()
                    )

                firestore.collection("expenses")
                    .add(expenseData)
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Expense Saved 💰",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener {

                        Toast.makeText(
                            this,
                            "Saved locally but Firebase failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                // CLEAR FORM
                titleInput.text.clear()
                amountInput.text.clear()
                categoryInput.text.clear()

                selectedDate = ""
                startTime = ""
                endTime = ""
                imageUriString = ""

                dateBtn.text = "Select Date"
                startTimeBtn.text = "Start Time"
                endTimeBtn.text = "End Time"

                previewImage.setImageDrawable(null)

            } else {

                Toast.makeText(
                    this,
                    "Error saving expense",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (
            requestCode == IMAGE_REQUEST_CODE &&
            resultCode == RESULT_OK
        ) {

            val imageUri = data?.data

            val previewImage =
                findViewById<ImageView>(
                    R.id.previewImage
                )

            previewImage.setImageURI(imageUri)

            imageUriString =
                imageUri.toString()
        }
    }
}