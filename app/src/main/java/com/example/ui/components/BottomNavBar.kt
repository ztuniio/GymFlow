package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Workout : BottomNavItem("workout", "Workout", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter)
    object Progress : BottomNavItem("progress", "Progress", Icons.Filled.ShowChart, Icons.Outlined.ShowChart)
    object Profile : BottomNavItem("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun GymBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Workout,
        BottomNavItem.Progress,
        BottomNavItem.Profile
    )

    NavigationBar(
        modifier = modifier
            .background(GymCardBackground)
            .windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = GymCardBackground,
        contentColor = GymTextPrimary,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 12.sp,
                        color = if (isSelected) GymAccent else GymTextSecondary
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GymBackground,
                    selectedTextColor = GymAccent,
                    indicatorColor = GymAccent,
                    unselectedIconColor = GymTextSecondary,
                    unselectedTextColor = GymTextSecondary
                ),
                modifier = Modifier.testTag("nav_item_${item.route}")
            )
        }
    }
}
