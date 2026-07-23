package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.FitnessCalculatorsSheet
import com.example.ui.components.GymBottomNavBar
import com.example.ui.screens.*
import com.example.ui.theme.GymBackground
import com.example.ui.theme.GymFlowTheme
import com.example.ui.viewmodel.GymViewModel

class MainActivity : ComponentActivity() {

    private val gymViewModel: GymViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GymFlowTheme {
                GymFlowApp(viewModel = gymViewModel)
            }
        }
    }
}

@Composable
fun GymFlowApp(viewModel: GymViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    var showCalculatorsSheet by remember { mutableStateOf(false) }
    val userProfile by viewModel.userProfile.collectAsState()

    val profile = userProfile ?: com.example.data.model.UserProfile()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = GymBackground,
        bottomBar = {
            if (currentRoute != "active_workout") {
                GymBottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToWorkout = { navController.navigate("workout") },
                    onNavigateToProgress = { navController.navigate("progress") },
                    onOpenCalculators = { showCalculatorsSheet = true }
                )
            }

            composable("workout") {
                WorkoutScreen(
                    viewModel = viewModel,
                    onStartActiveWorkout = { initialExercises ->
                        viewModel.startNewWorkout(
                            title = if (initialExercises.isNotEmpty()) "${initialExercises.first().category} Workout" else "Gym Session",
                            initialExercises = initialExercises
                        )
                        navController.navigate("active_workout")
                    }
                )
            }

            composable("active_workout") {
                ActiveWorkoutScreen(
                    viewModel = viewModel,
                    onFinishWorkout = {
                        navController.popBackStack("home", inclusive = false)
                    }
                )
            }

            composable("progress") {
                ProgressScreen(viewModel = viewModel)
            }

            composable("profile") {
                ProfileScreen(viewModel = viewModel)
            }
        }

        // Fitness Calculators Sheet Modal
        if (showCalculatorsSheet) {
            FitnessCalculatorsSheet(
                initialWeightKg = profile.weightKg,
                initialHeightCm = profile.heightCm,
                onDismiss = { showCalculatorsSheet = false }
            )
        }
    }
}
