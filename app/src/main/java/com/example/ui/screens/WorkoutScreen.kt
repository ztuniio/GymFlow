package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Exercise
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.GymViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    viewModel: GymViewModel,
    onStartActiveWorkout: (List<Exercise>) -> Unit,
    modifier: Modifier = Modifier
) {
    val exercises by viewModel.filteredExercises.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    val selectedForWorkout = remember { mutableStateListOf<Exercise>() }

    val categories = listOf("All", "Chest", "Back", "Shoulders", "Arms", "Legs", "Core", "Cardio")

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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Workout Library", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = GymTextPrimary)
                    Text("Choose exercises to log your training", fontSize = 13.sp, color = GymTextSecondary)
                }

                if (selectedForWorkout.isNotEmpty()) {
                    Button(
                        onClick = {
                            onStartActiveWorkout(selectedForWorkout.toList())
                            selectedForWorkout.clear()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GymAccent),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("start_workout_session_btn")
                    ) {
                        Text("Log (${selectedForWorkout.size})", color = GymBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search exercises...", color = GymTextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = GymTextSecondary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = GymTextSecondary)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GymAccent,
                    unfocusedBorderColor = GymGlassBorder,
                    focusedTextColor = GymTextPrimary,
                    unfocusedTextColor = GymTextPrimary,
                    focusedContainerColor = GymCardBackground,
                    unfocusedContainerColor = GymCardBackground
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_exercises_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setCategory(category) },
                        label = {
                            Text(
                                text = category,
                                color = if (isSelected) GymBackground else GymTextPrimary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GymAccent,
                            containerColor = GymCardBackground
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("category_chip_$category")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Exercise List
            if (exercises.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = GymTextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No exercises found", color = GymTextSecondary, fontSize = 16.sp)
                        Text("Try searching or switching categories", color = GymTextMuted, fontSize = 12.sp)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 80.dp)
                ) {
                    items(exercises, key = { it.id }) { exercise ->
                        val isSelected = selectedForWorkout.any { it.id == exercise.id }

                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = if (isSelected) GymPrimary.copy(alpha = 0.2f) else GymCardBackground,
                            borderColor = if (isSelected) GymAccent else GymGlassBorder,
                            onClick = {
                                if (isSelected) {
                                    selectedForWorkout.removeAll { it.id == exercise.id }
                                } else {
                                    selectedForWorkout.add(exercise)
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(GymPrimary.copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val icon = when (exercise.category) {
                                            "Chest" -> Icons.Default.FitnessCenter
                                            "Back" -> Icons.Default.FormatAlignJustify
                                            "Shoulders" -> Icons.Default.AccessibilityNew
                                            "Arms" -> Icons.Default.SportsMma
                                            "Legs" -> Icons.Default.DirectionsRun
                                            "Core" -> Icons.Default.SelfImprovement
                                            else -> Icons.Default.DirectionsBike
                                        }
                                        Icon(icon, contentDescription = null, tint = GymAccent, modifier = Modifier.size(24.dp))
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(exercise.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GymTextPrimary)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            "${exercise.category} • ${exercise.defaultSets} sets x ${exercise.defaultReps} reps • ${exercise.defaultWeightKg.toInt()}kg",
                                            fontSize = 12.sp,
                                            color = GymTextSecondary
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { viewModel.toggleFavorite(exercise) }) {
                                        Icon(
                                            if (exercise.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                                            contentDescription = "Favorite",
                                            tint = if (exercise.isFavorite) GymWarning else GymTextMuted
                                        )
                                    }

                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = {
                                            if (isSelected) {
                                                selectedForWorkout.removeAll { it.id == exercise.id }
                                            } else {
                                                selectedForWorkout.add(exercise)
                                            }
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = GymAccent,
                                            checkmarkColor = GymBackground
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // FAB to Add Custom Exercise
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = GymAccent,
            contentColor = GymBackground,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 90.dp, end = 20.dp)
                .testTag("add_custom_exercise_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Exercise")
        }
    }

    // Add Custom Exercise Dialog
    if (showAddDialog) {
        var nameInput by remember { mutableStateOf("") }
        var categoryInput by remember { mutableStateOf("Chest") }
        var setsInput by remember { mutableStateOf("4") }
        var repsInput by remember { mutableStateOf("10") }
        var weightInput by remember { mutableStateOf("20") }
        var descInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = GymCardBackground,
            title = { Text("Add Custom Exercise", color = GymTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Exercise Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GymAccent,
                            unfocusedBorderColor = GymGlassBorder,
                            focusedTextColor = GymTextPrimary,
                            unfocusedTextColor = GymTextPrimary
                        )
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = setsInput,
                            onValueChange = { setsInput = it },
                            label = { Text("Default Sets") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GymAccent,
                                unfocusedBorderColor = GymGlassBorder,
                                focusedTextColor = GymTextPrimary,
                                unfocusedTextColor = GymTextPrimary
                            )
                        )
                        OutlinedTextField(
                            value = repsInput,
                            onValueChange = { repsInput = it },
                            label = { Text("Default Reps") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GymAccent,
                                unfocusedBorderColor = GymGlassBorder,
                                focusedTextColor = GymTextPrimary,
                                unfocusedTextColor = GymTextPrimary
                            )
                        )
                    }

                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Default Weight (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GymAccent,
                            unfocusedBorderColor = GymGlassBorder,
                            focusedTextColor = GymTextPrimary,
                            unfocusedTextColor = GymTextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nameInput.isNotBlank()) {
                            viewModel.addCustomExercise(
                                name = nameInput,
                                category = categoryInput,
                                defaultSets = setsInput.toIntOrNull() ?: 4,
                                defaultReps = repsInput.toIntOrNull() ?: 10,
                                defaultWeightKg = weightInput.toFloatOrNull() ?: 20f,
                                description = descInput
                            )
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GymAccent)
                ) {
                    Text("Save", color = GymBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = GymTextSecondary)
                }
            }
        )
    }
}
