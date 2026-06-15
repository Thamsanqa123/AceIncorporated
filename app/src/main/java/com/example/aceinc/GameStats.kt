package com.example.aceinc

data class GameStats(

    val userId: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val budgetPoints: Int = 0,
    val badge: String = "Beginner"
)