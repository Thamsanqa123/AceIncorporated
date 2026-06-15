
# AceInc — Smart Expense Tracker

> **Track smarter. Save better. Boss your budget.**

AceInc is an Android expense tracking application built for students and young professionals who want to manage their finances while staying engaged through gamification. The app combines real-time expense logging, visual analytics, savings goal tracking, and a competitive tic-tac-toe mini-game — all backed by Firebase and local SQLite storage.

---
## Git: https://github.com/Thamsanqa123/AceIncorporated
## Demonstration vid:https://youtu.be/PSi0Tgtor3Y?si=7W1jZyUfsYoJ2gHU
## App Preview

> Screenshots below are taken directly from the running application.

| Screen | Preview |
|--------|---------|
| Animated Splash Screen | <img width="720" height="1600" alt="budget boss" src="https://github.com/user-attachments/assets/e77f0ecb-6f66-4dea-bc7c-6fdad70089fe" />|
| Account — Username Display | *(add screenshot)* |
| Dashboard | *(add screenshot)* |
| Add Expense | *(add screenshot)* |
| Budget Boss Game | *(add screenshot)* |

---

## Purpose & Problem Statement

Managing personal finances is a common struggle — especially for students. Most budgeting apps are either too complex, boring, or don't provide real-time feedback on spending habits.

**AceInc solves this by:**
- Making expense tracking fast and frictionless
- Giving users visual insight into where their money goes
- Rewarding good financial habits through a gamified points and rewards system
- Syncing data across devices using Firebase Firestore

---

---

## Own Feature 1 — Animated Splash Screen

### What It Does
When the app is launched, users are greeted by a fully animated splash screen rather than a plain static logo. The screen:

- **Reveals the "ACE" logo letter by letter** using a recursive `Handler` loop that appends one character every 180ms
- **Fades in the tagline** smoothly using `View.animate().alpha(1f)` once the title is fully typed
- **Animates a glowing background pulse** using `View.animate().scaleX/scaleY` that loops between 1.0× and 1.2× scale with an `AccelerateDecelerateInterpolator`
- **Fills a progress bar from 0 → 100** over 3.5 seconds using `ObjectAnimator.ofInt`
- **Navigates automatically to `LandingActivity`** after 4 seconds via a `Handler.postDelayed` call

### Why It Was Built
A well-designed splash screen establishes brand identity, gives the app time to initialise resources, and creates a positive first impression. The letter-by-letter reveal mimics a typewriter effect that aligns with the "ACE" brand personality — sharp, deliberate, and professional.

### Code Implementation

---
kotlin
// SplashActivity.kt

private fun revealLogo() {
    val text = "ACE"
    var index = 0

    Handler(Looper.getMainLooper()).post(object : Runnable {
        override fun run() {
            if (index <= text.length) {
                aceText.text = text.substring(0, index)
                index++
                Handler(Looper.getMainLooper()).postDelayed(this, 180)
            } else {
                // Fade in the tagline once logo is complete
                taglineText.animate().alpha(1f).setDuration(1000).start()
            }
        }
    })
}

private fun animateLoading() {
    // Animate progress bar 0 → 100 over 3.5 seconds
    ObjectAnimator.ofInt(loadingBar, "progress", 0, 100).apply {
        duration = 3500
        start()
    }
    // Navigate to LandingActivity after 4 seconds
    Handler(Looper.getMainLooper()).postDelayed({
        startActivity(Intent(this, LandingActivity::class.java))
        finish()
    }, 4000)
}

private fun startGlowAnimation() {
    val glow = findViewById<View>(R.id.backgroundGlow)
    glow.animate()
        .scaleX(1.2f).scaleY(1.2f)
        .setDuration(1800)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .withEndAction {
            glow.animate().scaleX(1f).scaleY(1f).setDuration(1800).start()
        }.start()
}

---


## Own Feature 2 — Personalised Username Display on Profile

### What It Does
After a user registers and logs in, the **Account screen** dynamically retrieves and displays their username from the local SQLite database. Specifically:

- The **username is queried from the database** using `db.getUser(userId)` which returns the stored user record
- The **username is displayed** in a `TextView` (`profileName`) on the Account screen
- An **email is derived and shown** in a second `TextView` (`profileEmail`)
- If the user record cannot be found (e.g. invalid userId), a toast error is shown and the activity closes safely
- A **Logout button** clears the session and returns the user to `LoginActivity`

### Why It Was Built
Personalisation is a core UX principle. Showing the user's actual name on their profile screen confirms their identity, builds trust in the app, and makes the experience feel tailored to them rather than generic. It also demonstrates the full authentication loop: register → store → retrieve → display.

### Code Implementation

```
kotlin
// AccountActivity.kt

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_account)

    db = DatabaseHelper(this)

    val usernameText = findViewById<TextView>(R.id.profileName)
    val emailText    = findViewById<TextView>(R.id.profileEmail)

    // Step 1: Get the userId passed from the previous activity
    userId = intent.getIntExtra("userId", -1)

    if (userId == -1) {
        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
        finish()
        return
    }

    // Step 2: Query the database for this user's record
    val user = db.getUser(userId)

    // Step 3: Display username and email on screen
    if (user != null) {
        val username = user.first
        usernameText.text = username
        emailText.text    = "$username@gmail.com"
    } else {
        Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show()
    }

    // Step 4: Logout — return to Login screen
    findViewById<Button>(R.id.logoutBtn).setOnClickListener {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
```

### Data Flow — How the Username Reaches the Screen

```
SignupActivity
    │  User registers → db.registerUser(username, email, password)
    │
    ▼
SQLite Database (users table)
    │  Stores: id | username | email | password
    │
LoginActivity
    │  User logs in → userId = db.loginUser(username, password)
    │  Passes userId via Intent to HomeActivity
    │
    ▼
HomeActivity → AccountActivity
    │  intent.putExtra("userId", userId)
    │
    ▼
AccountActivity
    │  userId = intent.getIntExtra("userId", -1)
    │  user   = db.getUser(userId)          ← retrieves from SQLite
    └──► usernameText.text = user.first     ← displays on screen
```

## All Features

### Expense Management
- Add expenses with title, amount, category, date, start/end time, and optional receipt image
- View all expenses in a scrollable RecyclerView with edit and delete support
- Confirmation dialog before deleting an expense

### Dashboard & Analytics
- Live balance summary showing total spend
- Highest, lowest, and average expense breakdown
- Smooth line chart visualising spending vs. budget goal (MPAndroidChart)
- Budget goal range display with remaining budget and over-budget warning

### Savings Goals
- Set minimum and maximum budget goals
- Real-time remaining budget calculation
- Visual status indicator ("Within budget" vs "Over Budget!")

### Budget Boss Game (Gamification)
- Tic-tac-toe mini-game against an AI opponent
- Budget points earned per win
- Milestone reward badges:
  - Bronze Saver — 10 wins
  - Silver Saver — 25 wins
  - Gold Saver — 50 wins
  - Budget Boss — 100 wins
- Leaderboard to compete with other users
- Rewards screen to view unlocked badges

### ☁️ Cloud Sync (Firebase)
- Expenses synced to Firestore in real time
- Game stats (wins, losses, points) persisted to Firebase
- User profiles and rewards stored in the cloud
- Offline-first: data saves locally first, then syncs

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| Platform | Android (minSdk 24+) |
| Local DB | SQLite via `DatabaseHelper` |
| Cloud DB | Firebase Firestore |
| Charts | MPAndroidChart |
| Build Tool | Gradle |
| Version Control | Git + GitHub |

---

## 🏗️ App Architecture

```
AceInc/
├── Activities/
│   ├── SplashActivity          # Own Feature 1 — Animated splash screen
│   ├── AccountActivity         # Own Feature 2 — Username display & profile
│   ├── LandingActivity         # Welcome / onboarding
│   ├── LoginActivity           # Authentication
│   ├── SignupActivity          # User registration
│   ├── HomeActivity            # Main navigation hub
│   ├── DashboardActivity       # Analytics & summary
│   ├── AddExpenseActivity      # Expense entry form
│   ├── ViewExpensesActivity    # Expense list + CRUD
│   ├── EditExpenseActivity     # Inline expense editing
│   ├── StatsActivity           # Detailed statistics
│   ├── SaveGoalActivity        # Budget goal setting
│   ├── BudgetBossGame          # Tic-tac-toe game
│   ├── RewardsActivity         # Badge showcase
│   └── LeaderboardActivity     # Global rankings
│
├── Data/
│   ├── DatabaseHelper          # SQLite CRUD operations
│   ├── ExpenseModel            # Expense data model
│   ├── GameStats               # Game statistics model
│   └── FirebaseGameManager     # Firebase game sync
│
├── Adapters/
│   └── ExpenseAdapter          # RecyclerView adapter
│
└── res/layout/
    ├── activity_splash.xml
    ├── activity_account.xml
    ├── activity_*.xml
    ├── item_expense.xml
    └── dialog_edit_expense.xml
```

---

## Data Flow

```
User Input
    │
    ▼
Activity (UI Layer)
    │
    ├──► DatabaseHelper (SQLite — Local, Always Available)
    │
    └──► Firebase Firestore (Cloud — When Online)
              │
              └──► Real-time sync across devices
```

Expenses are always written to SQLite first, ensuring the app works offline. Firebase acts as a secondary sync layer — if it fails, the local save still succeeds and a toast notifies the user.

---

## Design Considerations

### 1. Offline-First Architecture
Local SQLite storage ensures the app remains fully functional without an internet connection. Firebase sync happens asynchronously and failures are handled gracefully with user-facing feedback.

### 2. Dual Storage Pattern
Every write operation saves to SQLite first and then mirrors to Firestore. This prevents data loss and allows for cross-device access when connectivity is restored.

### 3. Gamification for Engagement
Financial apps suffer from low retention. By embedding a tic-tac-toe game with real budget point rewards, AceInc keeps users returning daily — reinforcing the habit of expense tracking.

### 4. Separation of Concerns
Each activity handles a single responsibility. Database operations are abstracted into `DatabaseHelper`, Firebase sync is isolated into dedicated methods, and `ExpenseAdapter` handles only presentation logic.

### 5. Animated Splash Screen (Own Feature 1)
Rather than a static logo, `SplashActivity` uses chained `Handler` posts and `ObjectAnimator` to create a typewriter reveal, progress animation, and glowing background pulse — all running simultaneously using Android's animation APIs.

### 6. Persistent User Identity (Own Feature 2)
The `userId` integer is passed between every activity via `Intent.putExtra` / `getIntExtra`, ensuring the correct user's data is always loaded. This single-user-session pattern keeps the data model simple while supporting multiple accounts on the same device.

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 24+
- A Firebase project with Firestore enabled
- `google-services.json` placed in the `/app` directory

### Installation

```bash
# 1. Clone the repository
git clone https://github.com/your-username/aceinc.git

# 2. Open in Android Studio
# File > Open > select the cloned folder

# 3. Add Firebase config
# Place your google-services.json in /app/

# 4. Sync Gradle
# Click "Sync Now" in the notification bar

# 5. Run on device or emulator
# Press Shift + F10 or click the green Run button
```



## GitHub & GitHub Actions

### Repository Structure

```
.github/
└── workflows/
    ├── android-build.yml       # Build & compile check on every push
    └── android-release.yml     # APK build on tagged releases
```

### CI/CD with GitHub Actions

AceInc uses **GitHub Actions** to automate the build pipeline on every push and pull request to `main` and `develop`.

#### What the Pipeline Does

| Step | Purpose |
|------|---------|
| Checkout | Pulls the latest code from the branch |
| Set up JDK 17 | Ensures consistent Java version across all builds |
| Grant permissions | Makes the Gradle wrapper executable on Linux runners |
| Inject Firebase config | Restores `google-services.json` from GitHub Secrets |
| Assemble Debug | Compiles the full app and surfaces any build errors |
| Upload APK | Makes the compiled APK downloadable from the Actions tab |

#### GitHub Secrets Required

| Secret | Description |
|--------|-------------|
| `GOOGLE_SERVICES_JSON` | Full contents of `google-services.json` from Firebase Console |

---

## Security Notes

- `google-services.json` is listed in `.gitignore` and never committed
- Passwords are stored locally in SQLite (BCrypt hashing recommended before production)
- Firebase Firestore rules should restrict reads/writes to authenticated users only

---

## Author

**AceInc Development Team**  
Thamsanqa, Siyabonga, Khani
