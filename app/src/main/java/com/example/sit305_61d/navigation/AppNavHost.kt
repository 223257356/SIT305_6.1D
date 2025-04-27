package com.example.sit305_61d.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.sit305_61d.ui.screens.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sit305_61d.data.repository.QuizRepositoryImpl
import com.example.sit305_61d.viewmodel.TaskViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import com.example.sit305_61d.ui.screens.TaskScreen
import com.example.sit305_61d.viewmodel.ResultsViewModel
import com.example.sit305_61d.ui.screens.ResultsScreen

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object SignUp : Screen("signup")
    data object Interests : Screen("interests")
    data object Home : Screen("home")
    data object Task : Screen("task/{taskId}") {
        fun createRoute(taskId: String) = "task/$taskId"
    }
    data object Results : Screen("results/{taskId}") {
        fun createRoute(taskId: String) = "results/$taskId"
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Screen.Home.route) },
                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) }
            )
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = { navController.navigate(Screen.Interests.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Interests.route) {
            InterestsScreen(
                onInterestsSelected = { navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                } }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToTask = { taskId ->
                    Log.d("AppNavHost", "Navigating to TaskScreen with taskId: $taskId")
                    navController.navigate(Screen.Task.createRoute(taskId))
                }
            )
        }
        composable(
            route = Screen.Task.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val repository = QuizRepositoryImpl()
            val factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return TaskViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
            val taskViewModel: TaskViewModel = viewModel(factory = factory)

            val taskIdArg = backStackEntry.arguments?.getString("taskId")
            LaunchedEffect(taskIdArg) {
                if (taskIdArg != null) {
                    taskViewModel.initializeTask(taskIdArg)
                } else {
                    Log.e("AppNavHost", "taskId argument is null for TaskScreen route!")
                }
            }

            TaskScreen(
                viewModel = taskViewModel,
                onSubmit = {
                   val currentTaskId = taskIdArg ?: ""
                   if (currentTaskId.isNotEmpty()) {
                       navController.navigate(Screen.Results.createRoute(currentTaskId))
                   }
                }
            )
        }
        composable(
            route = Screen.Results.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ResultsViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return ResultsViewModel(/* other deps? */) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
            val resultsViewModel: ResultsViewModel = viewModel(factory = factory)

            val taskIdArg = backStackEntry.arguments?.getString("taskId")
            LaunchedEffect(taskIdArg) {
                if (taskIdArg != null) {
                    resultsViewModel.initializeResults(taskIdArg)
                } else {
                    Log.e("AppNavHost", "taskId argument is null for ResultsScreen route!")
                }
            }

            ResultsScreen(
                viewModel = resultsViewModel,
                onContinue = { navController.popBackStack(Screen.Home.route, inclusive = false) }
            )
        }
    }
}
