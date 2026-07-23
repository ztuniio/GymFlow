package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.WaterDrop
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
import com.example.ui.theme.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessCalculatorsSheet(
    initialWeightKg: Float = 76.5f,
    initialHeightCm: Float = 178f,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: BMI, 1: Calories, 2: Water, 3: 1RM

    var heightInput by remember { mutableStateOf(initialHeightCm.toInt().toString()) }
    var weightInput by remember { mutableStateOf(initialWeightKg.toString()) }
    var ageInput by remember { mutableStateOf("24") }
    var isMale by remember { mutableStateOf(true) }

    // 1RM specific inputs
    var liftWeightInput by remember { mutableStateOf("80") }
    var repsInput by remember { mutableStateOf("8") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = GymCardBackground,
        scrimColor = Color.Black.copy(alpha = 0.7f),
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = GymGlassBorder)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fitness Calculators",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GymTextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = GymTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Calculator Mode Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = GymCardElevated,
                contentColor = GymAccent,
                edgePadding = 0.dp,
                divider = {},
                indicator = {}
            ) {
                val tabs = listOf(
                    "BMI" to Icons.Default.MonitorWeight,
                    "Calories" to Icons.Default.LocalFireDepartment,
                    "Water Intake" to Icons.Default.WaterDrop,
                    "1 Rep Max" to Icons.Default.FitnessCenter
                )
                tabs.forEachIndexed { idx, (label, icon) ->
                    Tab(
                        selected = selectedTab == idx,
                        onClick = { selectedTab = idx },
                        modifier = Modifier
                            .padding(4.dp)
                            .background(
                                color = if (selectedTab == idx) GymPrimary else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .testTag("calculator_tab_$idx"),
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    icon,
                                    contentDescription = null,
                                    tint = if (selectedTab == idx) GymTextPrimary else GymTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    label,
                                    color = if (selectedTab == idx) GymTextPrimary else GymTextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (selectedTab) {
                0 -> {
                    // BMI Calculator
                    val weight = weightInput.toFloatOrNull() ?: 70f
                    val height = heightInput.toFloatOrNull() ?: 175f
                    val heightM = height / 100f
                    val bmi = if (heightM > 0) weight / (heightM * heightM) else 0f
                    val (category, categoryColor) = when {
                        bmi < 18.5f -> "Underweight" to GymCyan
                        bmi in 18.5f..24.9f -> "Normal Weight" to GymSuccess
                        bmi in 25.0f..29.9f -> "Overweight" to GymWarning
                        else -> "Obese" to GymError
                    }

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = weightInput,
                                onValueChange = { weightInput = it },
                                label = { Text("Weight (kg)", color = GymTextSecondary) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GymAccent,
                                    unfocusedBorderColor = GymGlassBorder,
                                    focusedTextColor = GymTextPrimary,
                                    unfocusedTextColor = GymTextPrimary
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = heightInput,
                                onValueChange = { heightInput = it },
                                label = { Text("Height (cm)", color = GymTextSecondary) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GymAccent,
                                    unfocusedBorderColor = GymGlassBorder,
                                    focusedTextColor = GymTextPrimary,
                                    unfocusedTextColor = GymTextPrimary
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(GymCardElevated, RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("YOUR BMI SCORE", fontSize = 12.sp, color = GymTextMuted, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = String.format("%.1f", bmi),
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = categoryColor
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    color = categoryColor.copy(alpha = 0.15f),
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = category,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = categoryColor,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Calories & Macros Calculator
                    val weight = weightInput.toFloatOrNull() ?: 75f
                    val height = heightInput.toFloatOrNull() ?: 178f
                    val age = ageInput.toIntOrNull() ?: 24
                    val bmr = if (isMale) {
                        (10 * weight) + (6.25f * height) - (5 * age) + 5
                    } else {
                        (10 * weight) + (6.25f * height) - (5 * age) - 161
                    }
                    val maintenance = (bmr * 1.55f).roundToInt()
                    val muscleGain = maintenance + 350
                    val fatLoss = maintenance - 400

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Gender:", color = GymTextSecondary, fontSize = 14.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = isMale,
                                    onClick = { isMale = true },
                                    label = { Text("Male") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GymPrimary,
                                        selectedLabelColor = GymTextPrimary
                                    )
                                )
                                FilterChip(
                                    selected = !isMale,
                                    onClick = { isMale = false },
                                    label = { Text("Female") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GymPrimary,
                                        selectedLabelColor = GymTextPrimary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = weightInput,
                                onValueChange = { weightInput = it },
                                label = { Text("Weight (kg)", color = GymTextSecondary) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GymAccent,
                                    unfocusedBorderColor = GymGlassBorder,
                                    focusedTextColor = GymTextPrimary,
                                    unfocusedTextColor = GymTextPrimary
                                )
                            )
                            OutlinedTextField(
                                value = ageInput,
                                onValueChange = { ageInput = it },
                                label = { Text("Age", color = GymTextSecondary) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GymAccent,
                                    unfocusedBorderColor = GymGlassBorder,
                                    focusedTextColor = GymTextPrimary,
                                    unfocusedTextColor = GymTextPrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            MacroTargetRow(title = "Maintenance Calories", calories = maintenance, color = GymCyan)
                            MacroTargetRow(title = "Muscle Gain Target", calories = muscleGain, color = GymSuccess)
                            MacroTargetRow(title = "Fat Loss Target", calories = fatLoss, color = GymOrange)
                        }
                    }
                }

                2 -> {
                    // Water Intake Calculator
                    val weight = weightInput.toFloatOrNull() ?: 75f
                    val recommendedMl = (weight * 35).roundToInt()

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = weightInput,
                            onValueChange = { weightInput = it },
                            label = { Text("Your Weight (kg)", color = GymTextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GymAccent,
                                unfocusedBorderColor = GymGlassBorder,
                                focusedTextColor = GymTextPrimary,
                                unfocusedTextColor = GymTextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(GymCardElevated, RoundedCornerShape(16.dp))
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.WaterDrop, contentDescription = null, tint = GymCyan, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("RECOMMENDED DAILY HYDRATION", fontSize = 12.sp, color = GymTextMuted, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$recommendedMl ml",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GymCyan
                                )
                                Text("≈ ${(recommendedMl / 250f).toInt()} standard glasses", fontSize = 13.sp, color = GymTextSecondary)
                            }
                        }
                    }
                }

                3 -> {
                    // 1 Rep Max Calculator
                    val weight = liftWeightInput.toFloatOrNull() ?: 80f
                    val reps = repsInput.toIntOrNull() ?: 8
                    // Epley Formula: W * (1 + R/30)
                    val oneRepMax = if (reps > 0) (weight * (1 + reps / 30f)).roundToInt() else 0

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = liftWeightInput,
                                onValueChange = { liftWeightInput = it },
                                label = { Text("Weight Lifted (kg)", color = GymTextSecondary) },
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
                                label = { Text("Reps Performed", color = GymTextSecondary) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GymAccent,
                                    unfocusedBorderColor = GymGlassBorder,
                                    focusedTextColor = GymTextPrimary,
                                    unfocusedTextColor = GymTextPrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(GymCardElevated, RoundedCornerShape(16.dp))
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("ESTIMATED 1-REP MAX (1RM)", fontSize = 12.sp, color = GymTextMuted, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$oneRepMax kg",
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GymAccent
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Epley Formula Estimation", fontSize = 12.sp, color = GymTextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroTargetRow(title: String, calories: Int, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GymCardElevated, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = GymTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Text("$calories kcal", color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}
