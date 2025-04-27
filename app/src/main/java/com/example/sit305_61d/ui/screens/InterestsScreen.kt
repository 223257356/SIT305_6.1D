package com.example.sit305_61d.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sit305_61d.ui.theme.*
import com.example.sit305_61d.viewmodel.InterestsViewModel

// Dummy data removed, now comes from ViewModel
// val dummyInterests = ...

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InterestsScreen(
    onInterestsSelected: () -> Unit,
    viewModel: InterestsViewModel = viewModel() // Inject ViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val maxSelection = 10 // Keep for display text

    // Handle navigation trigger
    LaunchedEffect(uiState.navigateToHome) {
        if (uiState.navigateToHome) {
            onInterestsSelected() // Call the navigation lambda passed to the screen
            viewModel.onNavigationHandled() // Reset the flag
        }
    }

    // Show error messages
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
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
                // No scroll needed if loading takes full screen, or handle differently
                // Add scroll if content might overflow AND not loading
                .then(
                    if (!uiState.isLoading) Modifier.verticalScroll(rememberScrollState())
                    else Modifier
                )
        ) {
            if (uiState.isLoading && uiState.availableInterests.isEmpty()) {
                // Show a loading indicator covering the screen if initially loading
                Spacer(modifier = Modifier.weight(1f))
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.weight(1f))
            } else {
                // Screen Content
                Spacer(modifier = Modifier.height(64.dp))
                Text(
                    text = "Your Interests",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColorPrimary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You may select up to $maxSelection topics",
                    fontSize = 16.sp,
                    color = TextColorSecondary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(32.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 2
                ) {
                    uiState.availableInterests.forEach { interest ->
                        val isSelected = uiState.selectedInterests.contains(interest)
                        FilterChip(
                            selected = isSelected,
                            onClick = { if (!uiState.isLoading) viewModel.onInterestSelected(interest) },
                            label = { Text(interest) },
                            enabled = !uiState.isLoading, // Disable chip when loading
                            shape = RoundedCornerShape(8.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = TextFieldBackgroundColor,
                                labelColor = TextFieldTextColor,
                                selectedContainerColor = AppPrimaryColor,
                                selectedLabelColor = Color.White,
                                disabledContainerColor = TextFieldBackgroundColor.copy(alpha = 0.5f),
                                disabledLabelColor = TextFieldTextColor.copy(alpha = 0.5f)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = !uiState.isLoading,
                                selected = isSelected,
                                borderColor = Color.Gray,
                                selectedBorderColor = Color.Transparent,
                                borderWidth = 1.dp,
                                selectedBorderWidth = 0.dp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f)) // Push button to bottom

                Button(
                    onClick = viewModel::onNextClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    enabled = uiState.selectedInterests.isNotEmpty() && !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = AppPrimaryColor)
                ) {
                    if (uiState.isLoading && uiState.availableInterests.isNotEmpty()) {
                        // Show loading indicator on button only when submitting, not initial load
                        CircularProgressIndicator(
                             modifier = Modifier.size(24.dp),
                             color = Color.White,
                             strokeWidth = 3.dp
                        )
                    } else {
                        Text("Next", color = Color.White, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InterestsScreenPreview() {
    SIT305_61DTheme {
        InterestsScreen(onInterestsSelected = {})
    }
}
