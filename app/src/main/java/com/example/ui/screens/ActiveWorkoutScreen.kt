package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Exercise
import com.example.ui.components.ConfettiOverlay
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.GymViewModel
import com.example.ui.viewmodel.WorkoutSetInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    viewModel: GymViewModel,
    onFinishWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeState by viewModel.activeWorkout.collectAsState()
    var notesText by remember { mutableStateOf(activeState.notes) }

    val formattedWorkoutTime = remember(activeState.elapsedSeconds) {
        val mins = activeState.elapsedSeconds / 60
        val secs = activeState.elapsedSeconds % 60
        String.format("%02d:%02d", mins, secs)
    }

    val formattedRestTime = remember(activeState.restTimerSeconds) {
        val mins = activeState.restTimerSeconds / 60
        val secs = activeState.restTimerSeconds % 60
        String.format("%02d:%02d", mins, secs)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GymBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.cancelActiveWorkout(); onFinishWorkout() }) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel Workout", tint = GymTextSecondary)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = activeState.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GymTextPrimary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = GymAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formattedWorkoutTime,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GymAccent
                        )
                    }
                }

                Button(
                    onClick = {
                        viewModel.completeWorkout(notesText)
                        onFinishWorkout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GymSuccess),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("complete_workout_btn")
                ) {
                    Text("Finish", color = GymTextPrimary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rest Timer Control Bar
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = if (activeState.isRestTimerRunning) GymPrimary.copy(alpha = 0.25f) else GymCardBackground
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.HourglassTop,
                            contentDescription = "Rest Timer",
                            tint = if (activeState.isRestTimerRunning) GymAccent else GymTextSecondary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (activeState.isRestTimerRunning) "Resting... $formattedRestTime" else "Rest Timer",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GymTextPrimary
                            )
                            Text("Select rest duration:", fontSize = 11.sp, color = GymTextSecondary)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(30, 60, 90).forEach { sec ->
                            FilterChip(
                                selected = activeState.restTimerSeconds == sec && activeState.isRestTimerRunning,
                                onClick = { viewModel.startRestTimer(sec) },
                                label = { Text("${sec}s", fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GymAccent,
                                    selectedLabelColor = GymBackground,
                                    containerColor = GymCardElevated
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        if (activeState.isRestTimerRunning) {
                            IconButton(
                                onClick = { viewModel.stopRestTimer() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Stop, contentDescription = "Stop Timer", tint = GymError)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Exercise Sets Logger List
            if (activeState.selectedExercises.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No exercises added to session.", color = GymTextSecondary)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    itemsIndexed(activeState.selectedExercises) { _, exercise ->
                        val setInputs = activeState.exerciseSetsMap[exercise.id] ?: emptyList()

                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = exercise.name,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GymAccent
                                )
                                IconButton(
                                    onClick = { viewModel.removeExerciseFromActiveWorkout(exercise.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove Exercise", tint = GymTextMuted)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Table Header Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("SET", fontSize = 11.sp, color = GymTextMuted, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f))
                                Text("KG", fontSize = 11.sp, color = GymTextMuted, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                                Text("REPS", fontSize = 11.sp, color = GymTextMuted, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                                Text("DONE", fontSize = 11.sp, color = GymTextMuted, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            setInputs.forEachIndexed { setIdx, setInput ->
                                var weightText by remember(setInput.weightKg) { mutableStateOf(setInput.weightKg.toInt().toString()) }
                                var repsText by remember(setInput.reps) { mutableStateOf(setInput.reps.toString()) }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "#${setInput.setNumber}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GymTextPrimary,
                                        modifier = Modifier.weight(0.8f)
                                    )

                                    // Weight Input Field
                                    OutlinedTextField(
                                        value = weightText,
                                        onValueChange = {
                                            weightText = it
                                            val w = it.toFloatOrNull() ?: setInput.weightKg
                                            viewModel.updateWorkoutSet(exercise.id, setIdx, setInput.reps, w, setInput.isCompleted)
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = GymAccent,
                                            unfocusedBorderColor = GymGlassBorder,
                                            focusedTextColor = GymTextPrimary,
                                            unfocusedTextColor = GymTextPrimary
                                        ),
                                        modifier = Modifier
                                            .weight(1.5f)
                                            .height(48.dp)
                                            .padding(end = 4.dp)
                                    )

                                    // Reps Input Field
                                    OutlinedTextField(
                                        value = repsText,
                                        onValueChange = {
                                            repsText = it
                                            val r = it.toIntOrNull() ?: setInput.reps
                                            viewModel.updateWorkoutSet(exercise.id, setIdx, r, setInput.weightKg, setInput.isCompleted)
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = GymAccent,
                                            unfocusedBorderColor = GymGlassBorder,
                                            focusedTextColor = GymTextPrimary,
                                            unfocusedTextColor = GymTextPrimary
                                        ),
                                        modifier = Modifier
                                            .weight(1.5f)
                                            .height(48.dp)
                                            .padding(end = 4.dp)
                                    )

                                    // Complete Checkbox
                                    Checkbox(
                                        checked = setInput.isCompleted,
                                        onCheckedChange = { isChecked ->
                                            viewModel.updateWorkoutSet(
                                                exercise.id,
                                                setIdx,
                                                repsText.toIntOrNull() ?: setInput.reps,
                                                weightText.toFloatOrNull() ?: setInput.weightKg,
                                                isChecked
                                            )
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = GymSuccess,
                                            checkmarkColor = GymBackground
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Add Set Button
                            OutlinedButton(
                                onClick = { viewModel.addSetToExercise(exercise.id) },
                                border = androidx.compose.foundation.BorderStroke(1.dp, GymGlassBorder),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add Set", tint = GymAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add Set", color = GymTextPrimary, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // Confetti Celebration Overlay
        ConfettiOverlay(
            isVisible = activeState.showConfetti,
            onDismiss = { viewModel.dismissConfetti() }
        )
    }
}
