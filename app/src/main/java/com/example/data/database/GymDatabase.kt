package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.*
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfile::class,
        Exercise::class,
        WorkoutSession::class,
        WorkoutSetLog::class,
        Goal::class,
        Achievement::class,
        WeightLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GymDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun goalDao(): GoalDao
    abstract fun achievementDao(): AchievementDao
    abstract fun weightLogDao(): WeightLogDao

    companion object {
        @Volatile
        private var INSTANCE: GymDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): GymDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GymDatabase::class.java,
                    "gymflow_database"
                )
                .addCallback(GymDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class GymDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database)
                }
            }
        }

        suspend fun populateDatabase(db: GymDatabase) {
            // Populate User Profile
            db.userDao().insertOrUpdateProfile(
                UserProfile(
                    id = 1,
                    name = "Zaid",
                    age = 24,
                    heightCm = 178f,
                    weightKg = 76.5f,
                    goalWeightKg = 80.0f,
                    gymExperience = "Intermediate",
                    fitnessGoal = "Gain Muscle",
                    currentStreak = 5,
                    longestStreak = 14,
                    caloriesBurnedToday = 520,
                    waterIntakeMl = 2200,
                    waterTargetMl = 3000,
                    totalWorkoutsCompleted = 38,
                    totalVolumeLiftedKg = 42500.0,
                    totalHoursSpent = 46.5
                )
            )

            // Populate Preloaded Exercises
            val defaultExercises = listOf(
                // Chest
                Exercise(name = "Barbell Bench Press", category = "Chest", defaultSets = 4, defaultReps = 10, defaultWeightKg = 70f, description = "Flat barbell press for overall chest hypertrophy.", animationType = "BARBELL", isFavorite = true),
                Exercise(name = "Incline Dumbbell Press", category = "Chest", defaultSets = 4, defaultReps = 12, defaultWeightKg = 26f, description = "Upper chest focus with dumbbells.", animationType = "DUMBBELL", isFavorite = true),
                Exercise(name = "Cable Chest Fly", category = "Chest", defaultSets = 3, defaultReps = 15, defaultWeightKg = 15f, description = "Isolation chest movement for full stretch.", animationType = "MACHINE"),
                Exercise(name = "Pushups", category = "Chest", defaultSets = 4, defaultReps = 20, defaultWeightKg = 0f, description = "Bodyweight chest and tricep builder.", animationType = "BODYWEIGHT"),
                Exercise(name = "Dumbbell Pullover", category = "Chest", defaultSets = 3, defaultReps = 12, defaultWeightKg = 22f, description = "Chest expansion and lats activation.", animationType = "DUMBBELL"),

                // Back
                Exercise(name = "Deadlift", category = "Back", defaultSets = 4, defaultReps = 6, defaultWeightKg = 120f, description = "Compound posterior chain power movement.", animationType = "BARBELL", isFavorite = true),
                Exercise(name = "Lat Pulldown", category = "Back", defaultSets = 4, defaultReps = 10, defaultWeightKg = 60f, description = "Upper back width builder.", animationType = "MACHINE"),
                Exercise(name = "Bent Over Barbell Row", category = "Back", defaultSets = 4, defaultReps = 8, defaultWeightKg = 65f, description = "Mid-back thickness.", animationType = "BARBELL"),
                Exercise(name = "Seated Cable Row", category = "Back", defaultSets = 3, defaultReps = 12, defaultWeightKg = 55f, description = "Mid back squeeze and posture builder.", animationType = "MACHINE"),
                Exercise(name = "Pull-Ups", category = "Back", defaultSets = 4, defaultReps = 8, defaultWeightKg = 0f, description = "Bodyweight upper body builder.", animationType = "BODYWEIGHT"),

                // Shoulders
                Exercise(name = "Overhead Barbell Press", category = "Shoulders", defaultSets = 4, defaultReps = 8, defaultWeightKg = 45f, description = "Strict overhead shoulder press.", animationType = "BARBELL", isFavorite = true),
                Exercise(name = "Dumbbell Lateral Raise", category = "Shoulders", defaultSets = 4, defaultReps = 15, defaultWeightKg = 12f, description = "Side deltoid isolation for capped shoulders.", animationType = "DUMBBELL", isFavorite = true),
                Exercise(name = "Face Pulls", category = "Shoulders", defaultSets = 4, defaultReps = 15, defaultWeightKg = 20f, description = "Rear delts and rotator cuff health.", animationType = "MACHINE"),
                Exercise(name = "Arnold Press", category = "Shoulders", defaultSets = 3, defaultReps = 10, defaultWeightKg = 18f, description = "Rotational dumbbell overhead press.", animationType = "DUMBBELL"),

                // Arms
                Exercise(name = "Barbell Bicep Curl", category = "Arms", defaultSets = 4, defaultReps = 10, defaultWeightKg = 30f, description = "Classic bicep mass builder.", animationType = "BARBELL", isFavorite = true),
                Exercise(name = "Tricep Rope Pushdown", category = "Arms", defaultSets = 4, defaultReps = 12, defaultWeightKg = 25f, description = "Lateral head tricep isolation.", animationType = "MACHINE"),
                Exercise(name = "Hammer Curls", category = "Arms", defaultSets = 3, defaultReps = 12, defaultWeightKg = 16f, description = "Brachialis and forearm strength.", animationType = "DUMBBELL"),
                Exercise(name = "Skull Crushers", category = "Arms", defaultSets = 3, defaultReps = 10, defaultWeightKg = 28f, description = "Lying EZ-bar tricep extension.", animationType = "BARBELL"),

                // Legs
                Exercise(name = "Barbell Back Squat", category = "Legs", defaultSets = 4, defaultReps = 8, defaultWeightKg = 100f, description = "King of leg exercises for quads and glutes.", animationType = "BARBELL", isFavorite = true),
                Exercise(name = "Leg Press", category = "Legs", defaultSets = 4, defaultReps = 12, defaultWeightKg = 180f, description = "Heavy quad overload machine press.", animationType = "MACHINE"),
                Exercise(name = "Romanian Deadlift", category = "Legs", defaultSets = 3, defaultReps = 10, defaultWeightKg = 80f, description = "Hamstrings and glutes stretch focus.", animationType = "BARBELL"),
                Exercise(name = "Standing Calf Raises", category = "Legs", defaultSets = 4, defaultReps = 15, defaultWeightKg = 50f, description = "Calf muscle development.", animationType = "MACHINE"),

                // Core
                Exercise(name = "Hanging Leg Raise", category = "Core", defaultSets = 3, defaultReps = 15, defaultWeightKg = 0f, description = "Lower abdominal & hip flexor strength.", animationType = "BODYWEIGHT"),
                Exercise(name = "Cable Ab Crunch", category = "Core", defaultSets = 4, defaultReps = 15, defaultWeightKg = 35f, description = "Weighted abdominal crunch.", animationType = "MACHINE"),
                Exercise(name = "Plank Hold", category = "Core", defaultSets = 3, defaultReps = 60, defaultWeightKg = 0f, description = "Isometric core stability.", animationType = "BODYWEIGHT"),

                // Cardio
                Exercise(name = "Treadmill Running", category = "Cardio", defaultSets = 1, defaultReps = 20, defaultWeightKg = 0f, description = "High intensity cardio burn.", animationType = "CARDIO"),
                Exercise(name = "Rowing Machine", category = "Cardio", defaultSets = 1, defaultReps = 15, defaultWeightKg = 0f, description = "Full body endurance cardio.", animationType = "CARDIO")
            )
            db.exerciseDao().insertExercises(defaultExercises)

            // Populate Past Workout Sessions
            val now = System.currentTimeMillis()
            val dayMillis = 86400000L
            val session1Id = db.workoutDao().insertSession(
                WorkoutSession(
                    title = "Chest & Triceps Focus",
                    dateMillis = now - dayMillis * 1,
                    durationMinutes = 55,
                    caloriesBurned = 480,
                    notes = "Felt super energetic today! Hit new PR on Bench Press."
                )
            )
            db.workoutDao().insertSetLogs(
                listOf(
                    WorkoutSetLog(sessionId = session1Id, exerciseName = "Barbell Bench Press", category = "Chest", setNumber = 1, reps = 10, weightKg = 70f),
                    WorkoutSetLog(sessionId = session1Id, exerciseName = "Barbell Bench Press", category = "Chest", setNumber = 2, reps = 10, weightKg = 75f),
                    WorkoutSetLog(sessionId = session1Id, exerciseName = "Barbell Bench Press", category = "Chest", setNumber = 3, reps = 8, weightKg = 80f),
                    WorkoutSetLog(sessionId = session1Id, exerciseName = "Incline Dumbbell Press", category = "Chest", setNumber = 1, reps = 12, weightKg = 26f),
                    WorkoutSetLog(sessionId = session1Id, exerciseName = "Tricep Rope Pushdown", category = "Arms", setNumber = 1, reps = 12, weightKg = 25f)
                )
            )

            val session2Id = db.workoutDao().insertSession(
                WorkoutSession(
                    title = "Back & Biceps Power",
                    dateMillis = now - dayMillis * 2,
                    durationMinutes = 60,
                    caloriesBurned = 510,
                    notes = "Focused on controlled eccentric phase."
                )
            )
            db.workoutDao().insertSetLogs(
                listOf(
                    WorkoutSetLog(sessionId = session2Id, exerciseName = "Deadlift", category = "Back", setNumber = 1, reps = 6, weightKg = 120f),
                    WorkoutSetLog(sessionId = session2Id, exerciseName = "Lat Pulldown", category = "Back", setNumber = 1, reps = 10, weightKg = 60f),
                    WorkoutSetLog(sessionId = session2Id, exerciseName = "Barbell Bicep Curl", category = "Arms", setNumber = 1, reps = 10, weightKg = 30f)
                )
            )

            val session3Id = db.workoutDao().insertSession(
                WorkoutSession(
                    title = "Heavy Leg Day",
                    dateMillis = now - dayMillis * 4,
                    durationMinutes = 50,
                    caloriesBurned = 550,
                    notes = "Legs were burning on 4th squat set!"
                )
            )
            db.workoutDao().insertSetLogs(
                listOf(
                    WorkoutSetLog(sessionId = session3Id, exerciseName = "Barbell Back Squat", category = "Legs", setNumber = 1, reps = 8, weightKg = 100f),
                    WorkoutSetLog(sessionId = session3Id, exerciseName = "Leg Press", category = "Legs", setNumber = 1, reps = 12, weightKg = 180f)
                )
            )

            // Populate Weight History
            db.weightLogDao().insertWeightLog(WeightLog(dateMillis = now - dayMillis * 30, weightKg = 74.2f))
            db.weightLogDao().insertWeightLog(WeightLog(dateMillis = now - dayMillis * 20, weightKg = 75.0f))
            db.weightLogDao().insertWeightLog(WeightLog(dateMillis = now - dayMillis * 10, weightKg = 75.8f))
            db.weightLogDao().insertWeightLog(WeightLog(dateMillis = now - dayMillis * 5, weightKg = 76.2f))
            db.weightLogDao().insertWeightLog(WeightLog(dateMillis = now, weightKg = 76.5f))

            // Populate Goals
            db.goalDao().insertGoal(Goal(title = "Gain Muscle Mass", category = "Weight", targetValue = 80.0f, currentValue = 76.5f, unit = "kg"))
            db.goalDao().insertGoal(Goal(title = "Weekly Workout Frequency", category = "Workout", targetValue = 5f, currentValue = 4f, unit = "days/wk"))
            db.goalDao().insertGoal(Goal(title = "Bench Press PR", category = "Strength", targetValue = 100f, currentValue = 80f, unit = "kg"))
            db.goalDao().insertGoal(Goal(title = "Daily Water Intake", category = "Water", targetValue = 3000f, currentValue = 2200f, unit = "ml"))

            // Populate Achievements
            val defaultAchievements = listOf(
                Achievement(title = "First Workout", description = "Completed your very first workout in GymFlow!", badgeIconName = "STAR", progress = 1, target = 1, isUnlocked = true, unlockedDate = "2026-07-01"),
                Achievement(title = "7 Days Streak", description = "Logged workouts for 7 consecutive days.", badgeIconName = "FIRE", progress = 5, target = 7, isUnlocked = false),
                Achievement(title = "30 Days Streak", description = "Maintained a strong 30-day fitness habit.", badgeIconName = "FIRE", progress = 5, target = 30, isUnlocked = false),
                Achievement(title = "100 Workouts Club", description = "Completed 100 total gym sessions.", badgeIconName = "TROPHY", progress = 38, target = 100, isUnlocked = false),
                Achievement(title = "Volume Titan", description = "Lifted over 50,000 kg total cumulative volume.", badgeIconName = "DUMBBELL", progress = 42500, target = 50000, isUnlocked = false)
            )
            db.achievementDao().insertAchievements(defaultAchievements)
        }
    }
}
