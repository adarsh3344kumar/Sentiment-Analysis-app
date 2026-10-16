package com.example.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.AnalyzeScreen
import com.example.ui.screens.EvaluationScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TestingDashboardScreen
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.TextSecondary

sealed class NavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    object Home : NavItem(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Psychology,
        unselectedIcon = Icons.Outlined.Psychology,
        testTag = "nav_item_home"
    )

    object History : NavItem(
        route = "history",
        title = "History",
        selectedIcon = Icons.Filled.History,
        unselectedIcon = Icons.Outlined.History,
        testTag = "nav_item_history"
    )

    object Analytics : NavItem(
        route = "analytics",
        title = "Analytics",
        selectedIcon = Icons.Filled.Analytics,
        unselectedIcon = Icons.Outlined.Analytics,
        testTag = "nav_item_analytics"
    )

    object Testing : NavItem(
        route = "testing",
        title = "Testing",
        selectedIcon = Icons.Filled.VerifiedUser,
        unselectedIcon = Icons.Outlined.VerifiedUser,
        testTag = "nav_item_testing"
    )

    object Settings : NavItem(
        route = "settings",
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        testTag = "nav_item_settings"
    )
}

@Composable
fun MainScreen(
    viewModel: SentimentViewModel = viewModel()
) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    var showEvaluationSubscreen by rememberSaveable { mutableStateOf(false) }

    val navItems = listOf(
        NavItem.Home,
        NavItem.History,
        NavItem.Analytics,
        NavItem.Testing,
        NavItem.Settings
    )

    androidx.activity.compose.BackHandler(enabled = showEvaluationSubscreen || selectedIndex != 0) {
        if (showEvaluationSubscreen) {
            showEvaluationSubscreen = false
        } else {
            selectedIndex = 0
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = Color.White,
                tonalElevation = 4.dp
            ) {
                navItems.forEachIndexed { index, item ->
                    val selected = selectedIndex == index && !showEvaluationSubscreen
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            selectedIndex = index
                            showEvaluationSubscreen = false
                        },
                        icon = {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 10.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = IndigoLight,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        if (showEvaluationSubscreen) {
            EvaluationScreen(
                onNavigateBack = { showEvaluationSubscreen = false },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            when (selectedIndex) {
                0 -> AnalyzeScreen(
                    viewModel = viewModel,
                    onNavigateToHistory = { selectedIndex = 1 },
                    modifier = Modifier.padding(innerPadding)
                )
                1 -> HistoryScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
                2 -> AnalyticsScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
                3 -> TestingDashboardScreen(modifier = Modifier.padding(innerPadding))
                4 -> SettingsScreen(
                    viewModel = viewModel,
                    onOpenEvaluation = { showEvaluationSubscreen = true },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
