package com.example.aceinc


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "AceDB", null, 5) { // ⬅️ VERSION BUMPED

    override fun onCreate(db: SQLiteDatabase) {

        // USERS
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS users(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE,
                email TEXT,
                password TEXT
            )
        """)

        // EXPENSES
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS expenses(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                userId INTEGER,
                title TEXT,
                amount REAL DEFAULT 0,
                category TEXT,
                date TEXT,
                startTime TEXT,
                endTime TEXT,
                imageUri TEXT,
                FOREIGN KEY(userId) REFERENCES users(id)
            )
        """)

        // GAME STATS
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS game_stats(
                userId INTEGER PRIMARY KEY,
                wins INTEGER DEFAULT 0,
                losses INTEGER DEFAULT 0,
                draws INTEGER DEFAULT 0,
                budgetPoints INTEGER DEFAULT 0,
                badge TEXT DEFAULT 'Beginner',
                FOREIGN KEY(userId) REFERENCES users(id)
            )
        """)

        // REWARDS
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS rewards(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                userId INTEGER,
                rewardName TEXT,
                unlocked INTEGER DEFAULT 0,
                FOREIGN KEY(userId) REFERENCES users(id)
            )
        """)

        // BUDGET GOALS (FIXED — ONLY ONCE, NO DUPLICATES)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS budget_goals(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                userId INTEGER UNIQUE,
                minGoal REAL,
                maxGoal REAL,
                FOREIGN KEY(userId) REFERENCES users(id)
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        db.execSQL("DROP TABLE IF EXISTS rewards")
        db.execSQL("DROP TABLE IF EXISTS game_stats")
        db.execSQL("DROP TABLE IF EXISTS budget_goals")
        db.execSQL("DROP TABLE IF EXISTS expenses")
        db.execSQL("DROP TABLE IF EXISTS users")

        onCreate(db)
    }

    // =========================
    // GAME STATS CORE
    // =========================

    fun initializeGameStats(userId: Int) {

        val values = ContentValues().apply {
            put("userId", userId)
            put("wins", 0)
            put("losses", 0)
            put("draws", 0)
            put("budgetPoints", 0)
            put("badge", "Beginner")
        }

        writableDatabase.insertWithOnConflict(
            "game_stats",
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE
        )
    }

    fun recordWin(userId: Int): Boolean {

        initializeGameStats(userId)

        val db = writableDatabase

        db.execSQL("""
        UPDATE game_stats
        SET wins = wins + 1
        WHERE userId = $userId
    """)

        val wins = getWins(userId)

        var pointEarned = false

        if (wins % 10 == 0) {

            db.execSQL("""
            UPDATE game_stats
            SET budgetPoints = budgetPoints + 1
            WHERE userId = $userId
        """)

            updateBadge(userId)

            pointEarned = true
        }

        return pointEarned
    }

    fun recordLoss(userId: Int) {

        initializeGameStats(userId)

        writableDatabase.execSQL("""
        UPDATE game_stats
        SET losses = losses + 1
        WHERE userId = $userId
    """)
    }

    fun recordDraw(userId: Int) {

        initializeGameStats(userId)

        writableDatabase.execSQL("""
        UPDATE game_stats
        SET draws = draws + 1
        WHERE userId = $userId
    """)
    }

    fun ensureGameStats(userId: Int) {
        val db = writableDatabase

        val cursor = db.rawQuery(
            "SELECT userId FROM game_stats WHERE userId=?",
            arrayOf(userId.toString())
        )

        if (!cursor.moveToFirst()) {
            val values = ContentValues().apply {
                put("userId", userId)
                put("wins", 0)
                put("losses", 0)
                put("draws", 0)
                put("budgetPoints", 0)
                put("badge", "Beginner")
            }

            db.insert("game_stats", null, values)
        }

        cursor.close()
    }

    fun getWins(userId: Int): Int {

        initializeGameStats(userId)

        val cursor = readableDatabase.rawQuery(
            "SELECT wins FROM game_stats WHERE userId=?",
            arrayOf(userId.toString())
        )

        var wins = 0

        if (cursor.moveToFirst()) {
            wins = cursor.getInt(0)
        }

        cursor.close()

        return wins
    }

    fun getLosses(userId: Int): Int {

        initializeGameStats(userId)

        val cursor = readableDatabase.rawQuery(
            "SELECT losses FROM game_stats WHERE userId=?",
            arrayOf(userId.toString())
        )

        var losses = 0

        if (cursor.moveToFirst()) {
            losses = cursor.getInt(0)
        }

        cursor.close()

        return losses
    }

    fun getDraws(userId: Int): Int {

        initializeGameStats(userId)

        val cursor = readableDatabase.rawQuery(
            "SELECT draws FROM game_stats WHERE userId=?",
            arrayOf(userId.toString())
        )

        var draws = 0

        if (cursor.moveToFirst()) {
            draws = cursor.getInt(0)
        }

        cursor.close()

        return draws
    }

    fun getBudgetPoints(userId: Int): Int {

        initializeGameStats(userId)

        val cursor = readableDatabase.rawQuery(
            "SELECT budgetPoints FROM game_stats WHERE userId=?",
            arrayOf(userId.toString())
        )

        var points = 0

        if (cursor.moveToFirst()) {
            points = cursor.getInt(0)
        }

        cursor.close()

        return points
    }

    fun getBadge(userId: Int): String {

        initializeGameStats(userId)

        val cursor = readableDatabase.rawQuery(
            "SELECT badge FROM game_stats WHERE userId=?",
            arrayOf(userId.toString())
        )

        var badge = "Beginner"

        if (cursor.moveToFirst()) {
            badge = cursor.getString(0)
        }

        cursor.close()

        return badge
    }

    fun updateBadge(userId: Int) {

        val points = getBudgetPoints(userId)

        val badge = when {

            points >= 50 -> "Financial King"

            points >= 20 -> "Money Master"

            points >= 10 -> "Budget Boss"

            points >= 5 -> "Saver"

            else -> "Beginner"
        }

        val values = ContentValues().apply {
            put("badge", badge)
        }

        writableDatabase.update(
            "game_stats",
            values,
            "userId=?",
            arrayOf(userId.toString())
        )
    }

    // =========================
    // USER SYSTEM
    // =========================

    fun registerUser(username: String, email: String, password: String): Boolean {

        val values = ContentValues().apply {
            put("username", username)
            put("email", email)
            put("password", password)
        }


        return writableDatabase.insert("users", null, values) != -1L
    }


    fun loginUser(username: String, password: String): Int {

        val cursor = readableDatabase.rawQuery(
            "SELECT id FROM users WHERE username=? AND password=?",
            arrayOf(username, password)
        )

        val userId =
            if (cursor.moveToFirst()) cursor.getInt(0) else -1

        cursor.close()
        return userId
    }

    fun getUser(userId: Int): Pair<String, String> {

        val cursor = readableDatabase.rawQuery(
            "SELECT username,email FROM users WHERE id=?",
            arrayOf(userId.toString())
        )

        var username = ""
        var email = ""

        if (cursor.moveToFirst()) {
            username = cursor.getString(0)
            email = cursor.getString(1)
        }

        cursor.close()

        return Pair(username, email)
    }

    // =========================
    // EXPENSE SYSTEM
    // =========================

    fun insertExpense(
        userId: Int,
        title: String,
        amount: Double,
        category: String,
        date: String,
        startTime: String,
        endTime: String,
        imageUri: String
    ): Boolean {

        val values = ContentValues().apply {
            put("userId", userId)
            put("title", title)
            put("amount", amount)
            put("category", category)
            put("date", date)
            put("startTime", startTime)
            put("endTime", endTime)
            put("imageUri", imageUri)
        }

        return writableDatabase.insert("expenses", null, values) != -1L
    }

    fun getExpenses(userId: Int): MutableList<ExpenseModel> {

        val list = mutableListOf<ExpenseModel>()

        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM expenses WHERE userId=? ORDER BY id DESC",
            arrayOf(userId.toString())
        )

        while (cursor.moveToNext()) {

            list.add(
                ExpenseModel(
                    id = cursor.getInt(0),
                    userId = cursor.getInt(1),
                    title = cursor.getString(2),
                    amount = cursor.getDouble(3),
                    category = cursor.getString(4),
                    date = cursor.getString(5),
                    startTime = cursor.getString(6),
                    endTime = cursor.getString(7),
                    imageUri = cursor.getString(8) ?: ""
                )
            )
        }

        cursor.close()
        return list
    }

    fun getExpenseById(expenseId: Int): ExpenseModel? {

        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM expenses WHERE id=?",
            arrayOf(expenseId.toString())
        )

        var expense: ExpenseModel? = null

        if (cursor.moveToFirst()) {

            expense = ExpenseModel(
                id = cursor.getInt(0),
                userId = cursor.getInt(1),
                title = cursor.getString(2),
                amount = cursor.getDouble(3),
                category = cursor.getString(4),
                date = cursor.getString(5),
                startTime = cursor.getString(6),
                endTime = cursor.getString(7),
                imageUri = cursor.getString(8) ?: ""
            )
        }

        cursor.close()
        return expense
    }

    fun updateExpenseBasic(
        id: Int,
        title: String,
        amount: Double,
        category: String
    ): Boolean {

        val values = ContentValues().apply {
            put("title", title)
            put("amount", amount)
            put("category", category)
        }

        return writableDatabase.update(
            "expenses",
            values,
            "id=?",
            arrayOf(id.toString())
        ) > 0
    }

    fun deleteExpense(id: Int): Boolean {
        return writableDatabase.delete(
            "expenses",
            "id=?",
            arrayOf(id.toString())
        ) > 0
    }

    // =========================
    // ANALYTICS
    // =========================

    fun getTotalExpenses(userId: Int): Double {

        val cursor = readableDatabase.rawQuery(
            "SELECT SUM(amount) FROM expenses WHERE userId=?",
            arrayOf(userId.toString())
        )

        val total =
            if (cursor.moveToFirst() && !cursor.isNull(0)) cursor.getDouble(0)
            else 0.0

        cursor.close()
        return total
    }


    fun getExpenseCount(userId: Int): Int {

        val cursor = readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM expenses WHERE userId=?",
            arrayOf(userId.toString())
        )

        var count = 0

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()
        return count
    }

    fun getHighestExpense(userId: Int): Double {

        val cursor = readableDatabase.rawQuery(
            "SELECT MAX(amount) FROM expenses WHERE userId=?",
            arrayOf(userId.toString())
        )

        var highest = 0.0

        if (cursor.moveToFirst()) {
            highest = cursor.getDouble(0)
        }

        cursor.close()
        return highest
    }

    fun getLowestExpense(userId: Int): Double {

        val cursor = readableDatabase.rawQuery(
            "SELECT MIN(amount) FROM expenses WHERE userId=?",
            arrayOf(userId.toString())
        )

        var lowest = 0.0

        if (cursor.moveToFirst()) {
            lowest = cursor.getDouble(0)
        }

        cursor.close()
        return lowest
    }

    fun getCategoryTotals(userId: Int): HashMap<String, Float> {

        val map = HashMap<String, Float>()

        val cursor = readableDatabase.rawQuery(
            """
            SELECT category, SUM(amount)
            FROM expenses
            WHERE userId=?
            GROUP BY category
            """,
            arrayOf(userId.toString())
        )

        while (cursor.moveToNext()) {
            map[cursor.getString(0)] = cursor.getDouble(1).toFloat()
        }

        cursor.close()
        return map
    }

    fun getAverageExpense(userId: Int): Double {

        val cursor = readableDatabase.rawQuery(
            """
        SELECT AVG(amount)
        FROM expenses
        WHERE userId=?
        """,
            arrayOf(userId.toString())
        )

        var average = 0.0

        if(cursor.moveToFirst()) {
            average = cursor.getDouble(0)
        }

        cursor.close()

        return average
    }
    fun unlockReward(
        userId: Int,
        rewardName: String
    ) {

        val values = ContentValues().apply {
            put("userId", userId)
            put("rewardName", rewardName)
            put("unlocked", 1)
        }

        writableDatabase.insert(
            "rewards",
            null,
            values
        )
    }
    fun getUnlockedRewards(
        userId: Int
    ): MutableList<String> {

        val rewards = mutableListOf<String>()

        val cursor = readableDatabase.rawQuery(
            """
        SELECT rewardName
        FROM rewards
        WHERE userId=?
        """,
            arrayOf(userId.toString())
        )

        while(cursor.moveToNext()) {

            rewards.add(
                cursor.getString(0)
            )
        }

        cursor.close()

        return rewards
    }
    // =========================
// BUDGET GOALS
// =========================

    fun saveBudgetGoal(userId: Int, minGoal: Double, maxGoal: Double): Boolean {

        val db = writableDatabase

        val values = ContentValues().apply {
            put("userId", userId)
            put("minGoal", minGoal)
            put("maxGoal", maxGoal)
        }

        // safer: replace instead of delete + insert chaos
        return db.insertWithOnConflict(
            "budget_goals",
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        ) != -1L
    }

    fun getBudgetGoal(userId: Int): Pair<Double, Double> {

        val cursor = readableDatabase.rawQuery(
            "SELECT minGoal, maxGoal FROM budget_goals WHERE userId=?",
            arrayOf(userId.toString())
        )

        var min = 0.0
        var max = 0.0

        if (cursor.moveToFirst()) {
            min = cursor.getDouble(0)
            max = cursor.getDouble(1)
        }

        cursor.close()
        return Pair(min, max)
    }

    fun getRemainingBudget(userId: Int): Double {
        val max = getBudgetGoal(userId).second
        val spent = getTotalExpenses(userId)
        return max - spent
    }
}