package com.example.aceinc

data class ExpenseModel(
    val id: Int,
    val userId: Int,
    val title: String,
    val amount: Double,
    val category: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val imageUri: String
)