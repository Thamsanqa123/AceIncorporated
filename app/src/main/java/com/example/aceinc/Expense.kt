package com.example.aceinc

data class Expense(
    val id: Int,
    val title: String,
    val amount: Double,
    val category: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val imageUri: String
)