package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Goal
import com.example.data.model.UserProfile
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.GymViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: GymViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val goals by viewModel.goals.collectAsState()
    val achievements by viewModel.achievements.collectAsState()

    val profile = userProfile ?: UserProfile()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(profile.notificationsEnabled) }
    var darkModeEnabled by remember { mutableStateOf(profile.isDarkMode) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GymBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .padding(bottom = 80.dp)
    ) {
        // Profile Header Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(GymPrimary, CircleShape)
                            .border(2.dp, GymAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile.name.take(1).uppercase(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = GymTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(profile.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = GymTextPrimary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                color = GymAccent.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = profile.gymExperience,
                                    fontSize = 11.sp,
                                    color = GymAccent,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Surface(
                                color = GymPrimary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = profile.fitnessGoal,
                                    fontSize = 11.sp,
                                    color = GymPurpleLight,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = { showEditProfileDialog = true },
                    modifier = Modifier.testTag("edit_profile_btn")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = GymTextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Personal Stats Banner Grid
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ProfileStatBox(title = "Age", value = "${profile.age}", unit = "yrs", modifier = Modifier.weight(1f))
            ProfileStatBox(title = "Height", value = "${profile.heightCm.toInt()}", unit = "cm", modifier = Modifier.weight(1f))
            ProfileStatBox(title = "Weight", value = "${profile.weightKg}", unit = "kg", modifier = Modifier.weight(1f))
            ProfileStatBox(title = "Target", value = "${profile.goalWeightKg}", unit = "kg", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Goal System Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Fitness Goals", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GymTextPrimary)
            TextButton(
                onClick = { showAddGoalDialog = true },
                modifier = Modifier.testTag("add_goal_btn")
            ) {
                Text("+ Add Goal", color = GymAccent, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (goals.isEmpty()) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("No goals set yet.", color = GymTextSecondary, fontSize = 14.sp)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                goals.forEach { goal ->
                    val progressFraction = (goal.currentValue / goal.targetValue.coerceAtLeast(1f)).coerceIn(0f, 1f)

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(goal.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GymTextPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { progressFraction },
                                    color = GymAccent,
                                    trackColor = GymCardElevated,
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .height(8.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${goal.currentValue.toInt()} / ${goal.targetValue.toInt()} ${goal.unit}",
                                    fontSize = 12.sp,
                                    color = GymTextSecondary
                                )
                            }

                            IconButton(onClick = { viewModel.deleteGoal(goal) }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Goal", tint = GymTextMuted)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Achievements & Badges Showcase
        Text("Achievements & Badges", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GymTextPrimary)
        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            achievements.forEach { achievement ->
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = if (achievement.isUnlocked) GymCardBackground else GymCardBackground.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    if (achievement.isUnlocked) GymOrange.copy(alpha = 0.2f) else GymCardElevated,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            val icon = when (achievement.badgeIconName) {
                                "FIRE" -> Icons.Default.LocalFireDepartment
                                "TROPHY" -> Icons.Default.EmojiEvents
                                "DUMBBELL" -> Icons.Default.FitnessCenter
                                else -> Icons.Default.Star
                            }
                            Icon(
                                icon,
                                contentDescription = null,
                                tint = if (achievement.isUnlocked) GymOrange else GymTextMuted
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(achievement.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GymTextPrimary)
                            Text(achievement.description, fontSize = 12.sp, color = GymTextSecondary)
                        }

                        if (achievement.isUnlocked) {
                            Surface(
                                color = GymSuccess.copy(alpha = 0.2f),
                                shape = CircleShape
                            ) {
                                Text(
                                    "Unlocked",
                                    fontSize = 11.sp,
                                    color = GymSuccess,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        } else {
                            Text(
                                "${achievement.progress}/${achievement.target}",
                                fontSize = 12.sp,
                                color = GymTextMuted,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Preferences & App Settings
        Text("App Settings", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GymTextPrimary)
        Spacer(modifier = Modifier.height(10.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DarkMode, contentDescription = null, tint = GymAccent)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Dark Glassmorphism Theme", color = GymTextPrimary, fontSize = 15.sp)
                    }
                    Switch(
                        checked = darkModeEnabled,
                        onCheckedChange = { darkModeEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = GymAccent, checkedTrackColor = GymPrimary)
                    )
                }

                Divider(color = GymGlassBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = GymAccent)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Workout Reminders", color = GymTextPrimary, fontSize = 15.sp)
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = GymAccent, checkedTrackColor = GymPrimary)
                    )
                }

                Divider(color = GymGlassBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, tint = GymAccent)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Export Data (JSON)", color = GymTextPrimary, fontSize = 15.sp)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = GymTextSecondary)
                }

                Divider(color = GymGlassBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = GymAccent)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("About GymFlow v1.0", color = GymTextPrimary, fontSize = 15.sp)
                    }
                    Text("1.0.0", color = GymTextMuted, fontSize = 12.sp)
                }
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        var nameInput by remember { mutableStateOf(profile.name) }
        var ageInput by remember { mutableStateOf(profile.age.toString()) }
        var heightInput by remember { mutableStateOf(profile.heightCm.toInt().toString()) }
        var weightInput by remember { mutableStateOf(profile.weightKg.toString()) }
        var goalWeightInput by remember { mutableStateOf(profile.goalWeightKg.toString()) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            containerColor = GymCardBackground,
            title = { Text("Edit Gym Profile", color = GymTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GymAccent,
                            unfocusedBorderColor = GymGlassBorder,
                            focusedTextColor = GymTextPrimary,
                            unfocusedTextColor = GymTextPrimary
                        )
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = ageInput,
                            onValueChange = { ageInput = it },
                            label = { Text("Age") },
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
                            value = heightInput,
                            onValueChange = { heightInput = it },
                            label = { Text("Height (cm)") },
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
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = weightInput,
                            onValueChange = { weightInput = it },
                            label = { Text("Weight (kg)") },
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
                            value = goalWeightInput,
                            onValueChange = { goalWeightInput = it },
                            label = { Text("Target (kg)") },
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
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateUserProfile(
                            name = nameInput.ifBlank { profile.name },
                            age = ageInput.toIntOrNull() ?: profile.age,
                            heightCm = heightInput.toFloatOrNull() ?: profile.heightCm,
                            weightKg = weightInput.toFloatOrNull() ?: profile.weightKg,
                            goalWeightKg = goalWeightInput.toFloatOrNull() ?: profile.goalWeightKg,
                            gymExperience = profile.gymExperience,
                            fitnessGoal = profile.fitnessGoal
                        )
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GymAccent)
                ) {
                    Text("Save", color = GymBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel", color = GymTextSecondary)
                }
            }
        )
    }

    // Add Goal Dialog
    if (showAddGoalDialog) {
        var goalTitleInput by remember { mutableStateOf("") }
        var targetValueInput by remember { mutableStateOf("5") }
        var unitInput by remember { mutableStateOf("days/wk") }

        AlertDialog(
            onDismissRequest = { showAddGoalDialog = false },
            containerColor = GymCardBackground,
            title = { Text("Add New Fitness Goal", color = GymTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = goalTitleInput,
                        onValueChange = { goalTitleInput = it },
                        label = { Text("Goal Title (e.g. Weekly Workouts)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GymAccent,
                            unfocusedBorderColor = GymGlassBorder,
                            focusedTextColor = GymTextPrimary,
                            unfocusedTextColor = GymTextPrimary
                        )
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = targetValueInput,
                            onValueChange = { targetValueInput = it },
                            label = { Text("Target") },
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
                            value = unitInput,
                            onValueChange = { unitInput = it },
                            label = { Text("Unit") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GymAccent,
                                unfocusedBorderColor = GymGlassBorder,
                                focusedTextColor = GymTextPrimary,
                                unfocusedTextColor = GymTextPrimary
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (goalTitleInput.isNotBlank()) {
                            viewModel.addGoal(
                                title = goalTitleInput,
                                category = "General",
                                targetValue = targetValueInput.toFloatOrNull() ?: 5f,
                                unit = unitInput
                            )
                            showAddGoalDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GymAccent)
                ) {
                    Text("Add Goal", color = GymBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddGoalDialog = false }) {
                    Text("Cancel", color = GymTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun ProfileStatBox(title: String, value: String, unit: String, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(title.uppercase(), fontSize = 10.sp, color = GymTextMuted, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GymTextPrimary)
            Text(unit, fontSize = 10.sp, color = GymTextSecondary)
        }
    }
}
