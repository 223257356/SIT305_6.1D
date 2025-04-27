package com.example.sit305_61d.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sit305_61d.ui.theme.*
import com.example.sit305_61d.viewmodel.HomeTaskUiModel
import com.example.sit305_61d.viewmodel.HomeViewModel

// Dummy Data Removed
// data class HomeTask(...)
// val dummyHomeTasks = ...

@Composable
fun HomeScreen(
    onNavigateToTask: (taskId: String) -> Unit,
    viewModel: HomeViewModel = viewModel() // Inject ViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Show error messages
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onErrorMessageHandled() // Reset the error message
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackgroundGradient) // Apply gradient
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp)) // Space for status bar

            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Hello,",
                        fontSize = 28.sp,
                        color = TextColorSecondary
                    )
                    // Use user name from state, provide default if empty
                    Text(
                        text = uiState.userName.ifEmpty { "User" },
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColorPrimary
                    )
                }
                Icon(
                    imageVector = Icons.Filled.AccountCircle, // Replace with profile pic later
                    contentDescription = "Profile",
                    modifier = Modifier.size(50.dp),
                    tint = TextColorPrimary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Task Due Notification (Calculate from state)
            val tasksDueCount = uiState.tasks.count { !it.isComplete }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Notifications, // Bell Icon
                    contentDescription = "Tasks Due Notification",
                    tint = TextColorPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "You have $tasksDueCount task${if (tasksDueCount != 1) "s" else ""} due",
                    color = TextColorPrimary,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Task List or Loading Indicator
            if (uiState.isLoading) {
                 Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                     CircularProgressIndicator()
                 }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(uiState.tasks, key = { it.id }) { task -> // Use key for better performance
                        TaskCard(task = task, onClick = { onNavigateToTask(task.id) })
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) } // Padding at bottom
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    task: HomeTaskUiModel, // Updated data class
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x4DFFFFFF)) // Semi-transparent white card
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (task.isAiGenerated) {
                    Text(
                        text = "✨ Generated by AI", // Simple AI indicator
                        fontSize = 12.sp,
                        color = TextColorSecondary
                    )
                }
                Text(
                    text = task.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColorPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    fontSize = 14.sp,
                    color = TextColorSecondary,
                    maxLines = 2
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            // Status Indicator (Green circle like mockup)
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(if (task.isComplete) Color.Gray else AppPrimaryColor, CircleShape)
            ) // Simple colored circle. Add icons/progress later if needed.
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    SIT305_61DTheme {
        // Provide the lambda, use named args if needed
        HomeScreen(onNavigateToTask = {})
    }
}

// Preview for TaskCard uses the new model
@Preview(showBackground = true)
@Composable
fun TaskCardPreview() {
    val previewTask1 = HomeTaskUiModel(id = "p1", title = "Preview Task 1", description = "Desc 1", isAiGenerated = true, isComplete = false)
    val previewTask2 = HomeTaskUiModel(id = "p2", title = "Preview Task 2", description = "Desc 2", isAiGenerated = false, isComplete = true)
    SIT305_61DTheme {
        Column(Modifier.padding(16.dp)) {
            TaskCard(task = previewTask1, onClick = {})
            Spacer(modifier = Modifier.height(16.dp))
            TaskCard(task = previewTask2, onClick = {})
        }
    }
}
