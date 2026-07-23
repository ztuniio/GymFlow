package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.GymViewModel

@Composable
fun HomeScreen(
    viewModel: GymViewModel,
    onNavigateToWorkout: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onOpenCalculators: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val recentWorkouts by viewModel.recentWorkouts.collectAsState()
    val quote by viewModel.currentQuote.collectAsState()
    val greetingText = viewModel.getGreetingTimeText()

    val profile = userProfile ?: UserProfile()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GymBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Header Greeting
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = greetingText,
                    fontSize = 14.sp,
                    color = GymTextSecondary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${profile.name} 👋",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = GymTextPrimary
                    )
                }
            }

            // Profile Avatar / Streak Pill
            Surface(
                color = GymCardBackground,
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GymGlassBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = GymOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${profile.currentStreak} Days",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GymTextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Today's Workout Hero Banner
        GradientGlassCard(
            modifier = Modifier.fillMaxWidth(),
            gradientColors = listOf(GymPrimary, GymPrimaryVariant),
            onClick = onNavigateToWorkout
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = GymAccent.copy(alpha = 0.2f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = "TODAY'S TARGET",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GymAccent,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Upper Body Hypertrophy",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = GymTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "5 Exercises • 45 Mins • Chest & Arms",
                        fontSize = 12.sp,
                        color = GymTextPrimary.copy(alpha = 0.8f)
                    )
                }

                Button(
                    onClick = onNavigateToWorkout,
                    colors = ButtonDefaults.buttonColors(containerColor = GymAccent),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("quick_start_workout_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Start", color = GymBackground, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.PlayArrow, contentDescription = "Start", tint = GymBackground)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Metrics 2x2 Grid
        Text(
            text = "Daily Stats",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = GymTextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Calories Card
            GlassCard(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(GymOrange.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = GymOrange, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Calories", fontSize = 12.sp, color = GymTextSecondary)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("${profile.caloriesBurnedToday} kcal", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = GymTextPrimary)
                Text("Target 600", fontSize = 11.sp, color = GymTextMuted)
            }

            // Current Weight / BMI Card
            GlassCard(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(GymCyan.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = GymCyan, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Weight", fontSize = 12.sp, color = GymTextSecondary)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("${profile.weightKg} kg", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = GymTextPrimary)
                val bmi = profile.weightKg / ((profile.heightCm / 100f) * (profile.heightCm / 100f))
                Text("BMI ${String.format("%.1f", bmi)}", fontSize = 11.sp, color = GymSuccess)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Water Hydration Tracker Row
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(GymCyan.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.WaterDrop, contentDescription = "Water", tint = GymCyan)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Hydration", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GymTextPrimary)
                        Text("${profile.waterIntakeMl} / ${profile.waterTargetMl} ml", fontSize = 12.sp, color = GymTextSecondary)
                    }
                }

                IconButton(
                    onClick = { viewModel.addWaterIntake(250) },
                    modifier = Modifier
                        .background(GymCyan.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .testTag("add_water_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Water", tint = GymCyan)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Weekly Activity Bar Chart
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Weekly Activity",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GymTextPrimary
            )
            TextButton(onClick = onNavigateToProgress) {
                Text("See All", color = GymAccent, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            WeeklyWorkoutsBarChart()
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Motivational Quote Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = { viewModel.refreshMotivationalQuote() }
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(GymPrimary.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.FormatQuote, contentDescription = "Quote", tint = GymPrimary)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "\"${quote.first}\"",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = GymTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "— ${quote.second}",
                        fontSize = 12.sp,
                        color = GymAccent
                    )
                }
                Icon(Icons.Default.Refresh, contentDescription = "Refresh Quote", tint = GymTextMuted, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Calculators Entry Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = GymCardElevated,
            onClick = onOpenCalculators
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Calculate, contentDescription = null, tint = GymAccent)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Fitness Calculators", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GymTextPrimary)
                        Text("BMI, Macros, Water & 1-Rep Max", fontSize = 12.sp, color = GymTextSecondary)
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = GymTextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recent Workouts Section
        Text(
            text = "Recent Workouts",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = GymTextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (recentWorkouts.isEmpty()) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("No recent workouts logged yet.", fontSize = 14.sp, color = GymTextSecondary)
            }
        } else {
            recentWorkouts.take(3).forEach { workout ->
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
                        }
                        Icon(Icons.Default.CheckCircle, contentDescription = "Completed", tint = GymSuccess)
                    }
                }
            }
        }
    }
}
