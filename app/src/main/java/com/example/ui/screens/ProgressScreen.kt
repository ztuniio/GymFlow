package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.GymViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    viewModel: GymViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val weightLogs by viewModel.weightLogs.collectAsState()
    val allWorkouts by viewModel.allWorkouts.collectAsState()

    val profile = userProfile ?: UserProfile()
    var showWeightDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GymBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .padding(bottom = 80.dp)
    ) {
        // Screen Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Analytics & Progress", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = GymTextPrimary)
                Text("Track your gains, stats, and records", fontSize = 13.sp, color = GymTextSecondary)
            }

            IconButton(
                onClick = { showWeightDialog = true },
                modifier = Modifier
                    .background(GymAccent, CircleShape)
                    .testTag("log_weight_btn")
            ) {
                Icon(Icons.Default.Scale, contentDescription = "Log Weight", tint = GymBackground)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Key Statistics 3x2 Grid
        Text("Lifetime Statistics", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GymTextPrimary)
        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GlassCard(modifier = Modifier.weight(1f)) {
                Text("TOTAL WORKOUTS", fontSize = 10.sp, color = GymTextMuted, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${profile.totalWorkoutsCompleted}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = GymAccent)
            }
            GlassCard(modifier = Modifier.weight(1f)) {
                Text("TOTAL HOURS", fontSize = 10.sp, color = GymTextMuted, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(String.format("%.1f hrs", profile.totalHoursSpent), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = GymPrimary)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GlassCard(modifier = Modifier.weight(1f)) {
                Text("VOLUME LIFTED", fontSize = 10.sp, color = GymTextMuted, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(String.format("%.0f kg", profile.totalVolumeLiftedKg), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = GymOrange)
            }
            GlassCard(modifier = Modifier.weight(1f)) {
                Text("BEST STREAK", fontSize = 10.sp, color = GymTextMuted, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${profile.longestStreak} days", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = GymSuccess)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Weight Progress Chart
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Weight Trend", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GymTextPrimary)
                Text("Target: ${profile.goalWeightKg} kg", fontSize = 12.sp, color = GymAccent)
            }
            TextButton(onClick = { showWeightDialog = true }) {
                Text("+ Log Weight", color = GymAccent, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            val weightsList = weightLogs.map { it.weightKg }.ifEmpty { listOf(74.2f, 75.0f, 75.8f, 76.2f, profile.weightKg) }
            WeightProgressLineChart(weights = weightsList)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Personal Records
        Text("Personal Records (1RM)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GymTextPrimary)
        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            PRCard(exercise = "Barbell Bench Press", weightKg = "100 kg", reps = "1 Rep Max PR", color = GymAccent)
            PRCard(exercise = "Barbell Back Squat", weightKg = "125 kg", reps = "1 Rep Max PR", color = GymPrimary)
            PRCard(exercise = "Deadlift", weightKg = "150 kg", reps = "1 Rep Max PR", color = GymOrange)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Workout History Log
        Text("Workout History", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GymTextPrimary)
        Spacer(modifier = Modifier.height(10.dp))

        if (allWorkouts.isEmpty()) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("No workout history available yet.", color = GymTextSecondary, fontSize = 14.sp)
            }
        } else {
            allWorkouts.forEach { workout ->
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(workout.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GymTextPrimary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("${workout.durationMinutes} Mins • ${workout.caloriesBurned} kcal", fontSize = 12.sp, color = GymTextSecondary)
                            if (workout.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("\"${workout.notes}\"", fontSize = 11.sp, color = GymTextMuted)
                            }
                        }
                        IconButton(onClick = { viewModel.deleteSession(workout) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = GymError.copy(alpha = 0.7f))
                        }
                    }
                }
            }
        }
    }

    // Log Weight Dialog
    if (showWeightDialog) {
        var weightInput by remember { mutableStateOf(profile.weightKg.toString()) }

        AlertDialog(
            onDismissRequest = { showWeightDialog = false },
            containerColor = GymCardBackground,
            title = { Text("Log Current Weight", color = GymTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GymAccent,
                        unfocusedBorderColor = GymGlassBorder,
                        focusedTextColor = GymTextPrimary,
                        unfocusedTextColor = GymTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val w = weightInput.toFloatOrNull()
                        if (w != null && w > 0f) {
                            viewModel.logNewWeight(w)
                            showWeightDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GymAccent)
                ) {
                    Text("Save", color = GymBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWeightDialog = false }) {
                    Text("Cancel", color = GymTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun PRCard(exercise: String, weightKg: String, reps: String, color: Color) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(color.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(exercise, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GymTextPrimary)
                    Text(reps, fontSize = 11.sp, color = GymTextSecondary)
                }
            }
            Text(weightKg, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
