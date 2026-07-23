package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.GymDatabase
import com.example.data.model.*
import com.example.data.repository.GymRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class ActiveWorkoutState(
    val title: String = "Gym Session",
    val selectedExercises: List<Exercise> = emptyList(),
    val exerciseSetsMap: Map<Int, List<WorkoutSetInput>> = emptyMap(), // exerciseId -> list of set inputs
    val elapsedSeconds: Int = 0,
    val restTimerSeconds: Int = 0,
    val isRestTimerRunning: Boolean = false,
    val activeRestTimerTotal: Int = 60,
    val notes: String = "",
    val isWorkoutActive: Boolean = false,
    val showConfetti: Boolean = false
)

data class WorkoutSetInput(
    val setNumber: Int,
    val reps: Int,
    val weightKg: Float,
    val isCompleted: Boolean = false
)

class GymViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GymRepository
    val userProfile: StateFlow<UserProfile?>
    val allExercises: StateFlow<List<Exercise>>
    val recentWorkouts: StateFlow<List<WorkoutSession>>
    val allWorkouts: StateFlow<List<WorkoutSession>>
    val allSetLogs: StateFlow<List<WorkoutSetLog>>
    val goals: StateFlow<List<Goal>>
    val achievements: StateFlow<List<Achievement>>
    val weightLogs: StateFlow<List<WeightLog>>

    // Category and Search state
    val selectedCategory = MutableStateFlow("All")
    val searchQuery = MutableStateFlow("")

    // Filtered exercises
    val filteredExercises: StateFlow<List<Exercise>>

    // Active Workout Logger State
    private val _activeWorkout = MutableStateFlow(ActiveWorkoutState())
    val activeWorkout: StateFlow<ActiveWorkoutState> = _activeWorkout.asStateFlow()

    // Timer jobs
    private var workoutTimerJob: Job? = null
    private var restTimerJob: Job? = null

    // Motivational Quote state
    private val quotesList = listOf(
        "The only bad workout is the one that didn't happen." to "Unknown",
        "Action is the foundational key to all success." to "Pablo Picasso",
        "Success starts with self-discipline." to "Dwayne Johnson",
        "Don't count the days, make the days count." to "Muhammad Ali",
        "Consistency is what transforms average into excellence." to "GymFlow",
        "Your body can stand almost anything. It's your mind that you have to convince." to "Nike Training",
        "Push harder than yesterday if you want a different tomorrow." to "Strong"
    )
    private val _currentQuoteIndex = MutableStateFlow(0)
    val currentQuote: StateFlow<Pair<String, String>> = _currentQuoteIndex.map { quotesList[it] }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), quotesList[0])

    init {
        val db = GymDatabase.getDatabase(application, viewModelScope)
        repository = GymRepository(db)

        userProfile = repository.userProfile.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), null
        )
        allExercises = repository.allExercises.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        recentWorkouts = repository.recentWorkoutSessions.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allWorkouts = repository.allWorkoutSessions.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allSetLogs = repository.allSetLogs.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        goals = repository.allGoals.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        achievements = repository.allAchievements.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        weightLogs = repository.allWeightLogs.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        filteredExercises = combine(allExercises, selectedCategory, searchQuery) { exercises, category, query ->
            exercises.filter { ex ->
                (category == "All" || ex.category.equals(category, ignoreCase = true)) &&
                (query.isBlank() || ex.name.contains(query, ignoreCase = true) || ex.category.contains(query, ignoreCase = true))
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun setCategory(category: String) {
        selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun refreshMotivationalQuote() {
        _currentQuoteIndex.value = (_currentQuoteIndex.value + 1) % quotesList.size
    }

    fun toggleFavorite(exercise: Exercise) {
        viewModelScope.launch {
            repository.toggleFavoriteExercise(exercise.id, !exercise.isFavorite)
        }
    }

    fun addCustomExercise(name: String, category: String, defaultSets: Int, defaultReps: Int, defaultWeightKg: Float, description: String) {
        viewModelScope.launch {
            repository.addExercise(
                Exercise(
                    name = name,
                    category = category,
                    defaultSets = defaultSets,
                    defaultReps = defaultReps,
                    defaultWeightKg = defaultWeightKg,
                    description = description,
                    animationType = "BARBELL",
                    isCustom = true
                )
            )
        }
    }

    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch {
            repository.deleteExercise(exercise)
        }
    }

    // Workout Logger Functions
    fun startNewWorkout(title: String = "Gym Session", initialExercises: List<Exercise> = emptyList()) {
        val initialMap = initialExercises.associate { ex ->
            ex.id to List(ex.defaultSets) { setIdx ->
                WorkoutSetInput(setNumber = setIdx + 1, reps = ex.defaultReps, weightKg = ex.defaultWeightKg)
            }
        }
        _activeWorkout.value = ActiveWorkoutState(
            title = title,
            selectedExercises = initialExercises,
            exerciseSetsMap = initialMap,
            isWorkoutActive = true,
            elapsedSeconds = 0
        )
        startWorkoutTimer()
    }

    fun addExerciseToActiveWorkout(exercise: Exercise) {
        val current = _activeWorkout.value
        if (current.selectedExercises.none { it.id == exercise.id }) {
            val newList = current.selectedExercises + exercise
            val newSets = List(exercise.defaultSets) { idx ->
                WorkoutSetInput(setNumber = idx + 1, reps = exercise.defaultReps, weightKg = exercise.defaultWeightKg)
            }
            val newMap = current.exerciseSetsMap + (exercise.id to newSets)
            _activeWorkout.value = current.copy(
                selectedExercises = newList,
                exerciseSetsMap = newMap
            )
        }
    }

    fun removeExerciseFromActiveWorkout(exerciseId: Int) {
        val current = _activeWorkout.value
        val newList = current.selectedExercises.filter { it.id != exerciseId }
        val newMap = current.exerciseSetsMap - exerciseId
        _activeWorkout.value = current.copy(selectedExercises = newList, exerciseSetsMap = newMap)
    }

    fun updateWorkoutSet(exerciseId: Int, setIndex: Int, reps: Int, weightKg: Float, isCompleted: Boolean) {
        val current = _activeWorkout.value
        val sets = current.exerciseSetsMap[exerciseId]?.toMutableList() ?: return
        if (setIndex in sets.indices) {
            val oldSet = sets[setIndex]
            sets[setIndex] = oldSet.copy(reps = reps, weightKg = weightKg, isCompleted = isCompleted)
            val newMap = current.exerciseSetsMap + (exerciseId to sets)
            _activeWorkout.value = current.copy(exerciseSetsMap = newMap)

            // Auto start rest timer when set completed
            if (!oldSet.isCompleted && isCompleted) {
                startRestTimer(60)
            }
        }
    }

    fun addSetToExercise(exerciseId: Int) {
        val current = _activeWorkout.value
        val sets = current.exerciseSetsMap[exerciseId] ?: emptyList()
        val lastSet = sets.lastOrNull()
        val newSetNumber = sets.size + 1
        val newSet = WorkoutSetInput(
            setNumber = newSetNumber,
            reps = lastSet?.reps ?: 10,
            weightKg = lastSet?.weightKg ?: 20f
        )
        val newMap = current.exerciseSetsMap + (exerciseId to (sets + newSet))
        _activeWorkout.value = current.copy(exerciseSetsMap = newMap)
    }

    fun removeSetFromExercise(exerciseId: Int, setIndex: Int) {
        val current = _activeWorkout.value
        val sets = current.exerciseSetsMap[exerciseId]?.toMutableList() ?: return
        if (sets.size > 1 && setIndex in sets.indices) {
            sets.removeAt(setIndex)
            val renumbered = sets.mapIndexed { idx, s -> s.copy(setNumber = idx + 1) }
            val newMap = current.exerciseSetsMap + (exerciseId to renumbered)
            _activeWorkout.value = current.copy(exerciseSetsMap = newMap)
        }
    }

    private fun startWorkoutTimer() {
        workoutTimerJob?.cancel()
        workoutTimerJob = viewModelScope.launch {
            while (_activeWorkout.value.isWorkoutActive) {
                delay(1000)
                _activeWorkout.value = _activeWorkout.value.copy(
                    elapsedSeconds = _activeWorkout.value.elapsedSeconds + 1
                )
            }
        }
    }

    fun startRestTimer(seconds: Int) {
        restTimerJob?.cancel()
        _activeWorkout.value = _activeWorkout.value.copy(
            restTimerSeconds = seconds,
            activeRestTimerTotal = seconds,
            isRestTimerRunning = true
        )
        restTimerJob = viewModelScope.launch {
            while (_activeWorkout.value.restTimerSeconds > 0 && _activeWorkout.value.isRestTimerRunning) {
                delay(1000)
                val newSec = _activeWorkout.value.restTimerSeconds - 1
                _activeWorkout.value = _activeWorkout.value.copy(restTimerSeconds = newSec)
            }
            _activeWorkout.value = _activeWorkout.value.copy(isRestTimerRunning = false)
        }
    }

    fun stopRestTimer() {
        restTimerJob?.cancel()
        _activeWorkout.value = _activeWorkout.value.copy(isRestTimerRunning = false, restTimerSeconds = 0)
    }

    fun completeWorkout(notes: String = "") {
        val current = _activeWorkout.value
        if (!current.isWorkoutActive) return

        val durationMinutes = (current.elapsedSeconds / 60).coerceAtLeast(1)
        val completedSetLogs = mutableListOf<WorkoutSetLog>()
        var totalEstCalories = 0

        current.selectedExercises.forEach { exercise ->
            val sets = current.exerciseSetsMap[exercise.id] ?: emptyList()
            sets.forEach { setInput ->
                completedSetLogs.add(
                    WorkoutSetLog(
                        sessionId = 0,
                        exerciseName = exercise.name,
                        category = exercise.category,
                        setNumber = setInput.setNumber,
                        reps = setInput.reps,
                        weightKg = setInput.weightKg,
                        isCompleted = setInput.isCompleted
                    )
                )
            }
            totalEstCalories += (sets.count { it.isCompleted } * 25)
        }
        val finalCalories = totalEstCalories.coerceAtLeast(120)

        viewModelScope.launch {
            repository.logCompletedWorkout(
                title = current.title,
                durationMinutes = durationMinutes,
                caloriesBurned = finalCalories,
                notes = notes.ifBlank { current.notes },
                setLogs = completedSetLogs,
                currentProfile = userProfile.value
            )

            // Trigger celebration confetti
            _activeWorkout.value = current.copy(
                isWorkoutActive = false,
                showConfetti = true
            )

            workoutTimerJob?.cancel()
            restTimerJob?.cancel()

            delay(3000)
            _activeWorkout.value = ActiveWorkoutState()
        }
    }

    fun dismissConfetti() {
        _activeWorkout.value = _activeWorkout.value.copy(showConfetti = false)
    }

    fun cancelActiveWorkout() {
        workoutTimerJob?.cancel()
        restTimerJob?.cancel()
        _activeWorkout.value = ActiveWorkoutState()
    }

    // Profile & Goals Operations
    fun updateUserProfile(name: String, age: Int, heightCm: Float, weightKg: Float, goalWeightKg: Float, gymExperience: String, fitnessGoal: String) {
        val current = userProfile.value ?: UserProfile()
        viewModelScope.launch {
            repository.updateProfile(
                current.copy(
                    name = name,
                    age = age,
                    heightCm = heightCm,
                    weightKg = weightKg,
                    goalWeightKg = goalWeightKg,
                    gymExperience = gymExperience,
                    fitnessGoal = fitnessGoal
                )
            )
        }
    }

    fun addGoal(title: String, category: String, targetValue: Float, unit: String) {
        viewModelScope.launch {
            repository.addGoal(
                Goal(
                    title = title,
                    category = category,
                    targetValue = targetValue,
                    currentValue = 0f,
                    unit = unit
                )
            )
        }
    }

    fun updateGoalProgress(goal: Goal, newCurrent: Float) {
        viewModelScope.launch {
            val isCompleted = newCurrent >= goal.targetValue
            repository.updateGoal(goal.copy(currentValue = newCurrent, isCompleted = isCompleted))
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    fun logNewWeight(weightKg: Float) {
        viewModelScope.launch {
            repository.addWeightLog(weightKg, userProfile.value)
        }
    }

    fun addWaterIntake(ml: Int) {
        viewModelScope.launch {
            repository.updateWaterIntake(ml, userProfile.value)
        }
    }

    fun deleteSession(session: WorkoutSession) {
        viewModelScope.launch {
            repository.deleteSession(session)
        }
    }

    fun getGreetingTimeText(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Good Morning,"
            in 12..16 -> "Good Afternoon,"
            else -> "Good Evening,"
        }
    }
}
