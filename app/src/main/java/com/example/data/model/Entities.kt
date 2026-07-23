package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Zaid",
    val age: Int = 24,
    val heightCm: Float = 178f,
    val weightKg: Float = 76.5f,
    val goalWeightKg: Float = 80.0f,
    val gymExperience: String = "Intermediate",
    val fitnessGoal: String = "Gain Muscle",
    val currentStreak: Int = 5,
    val longestStreak: Int = 14,
    val caloriesBurnedToday: Int = 450,
    val waterIntakeMl: Int = 2200,
    val waterTargetMl: Int = 3000,
    val totalWorkoutsCompleted: Int = 38,
    val totalVolumeLiftedKg: Double = 42500.0,
    val totalHoursSpent: Double = 46.5,
    val isDarkMode: Boolean = true,
    val notificationsEnabled: Boolean = true
)

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String, // Chest, Back, Shoulders, Arms, Legs, Core, Cardio
    val defaultSets: Int = 4,
    val defaultReps: Int = 10,
    val defaultWeightKg: Float = 40f,
    val description: String = "",
    val animationType: String = "BARBELL", // BARBELL, DUMBBELL, MACHINE, BODYWEIGHT, CARDIO
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false
)

@Entity(tableName = "workout_sessions")
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val dateMillis: Long = System.currentTimeMillis(),
    val durationMinutes: Int = 45,
    val caloriesBurned: Int = 320,
    val notes: String = "",
    val isCompleted: Boolean = true
)

@Entity(tableName = "workout_set_logs")
data class WorkoutSetLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val exerciseName: String,
    val category: String,
    val setNumber: Int,
    val reps: Int,
    val weightKg: Float,
    val isCompleted: Boolean = true
)

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // Weight, Workout, Strength, Water
    val targetValue: Float,
    val currentValue: Float,
    val unit: String,
    val isCompleted: Boolean = false
)

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val badgeIconName: String, // e.g. "DUMBBELL", "FIRE", "TROPHY", "TARGET", "STAR"
    val progress: Int,
    val target: Int,
    val isUnlocked: Boolean = false,
    val unlockedDate: String? = null
)

@Entity(tableName = "weight_logs")
data class WeightLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateMillis: Long = System.currentTimeMillis(),
    val weightKg: Float
)
