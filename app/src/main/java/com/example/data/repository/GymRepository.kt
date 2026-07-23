package com.example.data.repository

import com.example.data.database.GymDatabase
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class GymRepository(private val db: GymDatabase) {

    val userProfile: Flow<UserProfile?> = db.userDao().getUserProfile()
    val allExercises: Flow<List<Exercise>> = db.exerciseDao().getAllExercises()
    val allWorkoutSessions: Flow<List<WorkoutSession>> = db.workoutDao().getAllSessions()
    val recentWorkoutSessions: Flow<List<WorkoutSession>> = db.workoutDao().getRecentSessions(5)
    val allSetLogs: Flow<List<WorkoutSetLog>> = db.workoutDao().getAllSetLogs()
    val allGoals: Flow<List<Goal>> = db.goalDao().getAllGoals()
    val allAchievements: Flow<List<Achievement>> = db.achievementDao().getAllAchievements()
    val allWeightLogs: Flow<List<WeightLog>> = db.weightLogDao().getAllWeightLogs()

    fun getExercisesByCategory(category: String): Flow<List<Exercise>> {
        return if (category == "All") {
            db.exerciseDao().getAllExercises()
        } else {
            db.exerciseDao().getExercisesByCategory(category)
        }
    }

    fun searchExercises(query: String): Flow<List<Exercise>> {
        return db.exerciseDao().searchExercises(query)
    }

    fun getSetLogsForSession(sessionId: Long): Flow<List<WorkoutSetLog>> {
        return db.workoutDao().getSetLogsForSession(sessionId)
    }

    suspend fun updateProfile(profile: UserProfile) {
        db.userDao().insertOrUpdateProfile(profile)
    }

    suspend fun addExercise(exercise: Exercise) {
        db.exerciseDao().insertExercise(exercise)
    }

    suspend fun toggleFavoriteExercise(exerciseId: Int, isFavorite: Boolean) {
        db.exerciseDao().updateFavorite(exerciseId, isFavorite)
    }

    suspend fun deleteExercise(exercise: Exercise) {
        db.exerciseDao().deleteExercise(exercise)
    }

    suspend fun logCompletedWorkout(
        title: String,
        durationMinutes: Int,
        caloriesBurned: Int,
        notes: String,
        setLogs: List<WorkoutSetLog>,
        currentProfile: UserProfile?
    ): Long {
        val session = WorkoutSession(
            title = title,
            dateMillis = System.currentTimeMillis(),
            durationMinutes = durationMinutes,
            caloriesBurned = caloriesBurned,
            notes = notes,
            isCompleted = true
        )
        val sessionId = db.workoutDao().insertSession(session)

        val updatedSetLogs = setLogs.map { log ->
            log.copy(sessionId = sessionId)
        }
        db.workoutDao().insertSetLogs(updatedSetLogs)

        // Update profile stats (streak, total workouts, volume, calories)
        currentProfile?.let { profile ->
            val addedVolume = setLogs.filter { it.isCompleted }.sumOf { (it.reps * it.weightKg).toDouble() }
            val updatedProfile = profile.copy(
                totalWorkoutsCompleted = profile.totalWorkoutsCompleted + 1,
                totalHoursSpent = profile.totalHoursSpent + (durationMinutes / 60.0),
                totalVolumeLiftedKg = profile.totalVolumeLiftedKg + addedVolume,
                caloriesBurnedToday = profile.caloriesBurnedToday + caloriesBurned,
                currentStreak = profile.currentStreak + 1,
                longestStreak = maxOf(profile.currentStreak + 1, profile.longestStreak)
            )
            db.userDao().insertOrUpdateProfile(updatedProfile)
        }

        return sessionId
    }

    suspend fun deleteSession(session: WorkoutSession) {
        db.workoutDao().deleteSession(session)
    }

    suspend fun addGoal(goal: Goal) {
        db.goalDao().insertGoal(goal)
    }

    suspend fun updateGoal(goal: Goal) {
        db.goalDao().updateGoal(goal)
    }

    suspend fun deleteGoal(goal: Goal) {
        db.goalDao().deleteGoal(goal)
    }

    suspend fun addWeightLog(weightKg: Float, currentProfile: UserProfile?) {
        db.weightLogDao().insertWeightLog(WeightLog(weightKg = weightKg))
        currentProfile?.let { profile ->
            db.userDao().insertOrUpdateProfile(profile.copy(weightKg = weightKg))
        }
    }

    suspend fun updateWaterIntake(addMl: Int, currentProfile: UserProfile?) {
        currentProfile?.let { profile ->
            val newWater = (profile.waterIntakeMl + addMl).coerceAtLeast(0)
            db.userDao().insertOrUpdateProfile(profile.copy(waterIntakeMl = newWater))
        }
    }
}
