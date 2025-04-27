package com.example.sit305_61d.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle // Example Icon
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle // Added
import androidx.lifecycle.viewmodel.compose.viewModel // Added
import com.example.sit305_61d.ui.theme.*
import com.example.sit305_61d.viewmodel.SignUpViewModel // Added
import androidx.compose.foundation.text.KeyboardOptions

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onNavigateBack: () -> Unit, // TODO: Implement back navigation
    viewModel: SignUpViewModel = viewModel() // Inject ViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Handle successful sign-up navigation
    LaunchedEffect(uiState.signUpSuccess) {
        if (uiState.signUpSuccess) {
            Toast.makeText(context, "Sign up successful!", Toast.LENGTH_SHORT).show()
            onSignUpSuccess()
            viewModel.onSignUpHandled() // Reset the flag
        }
    }

    // Show error messages
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show() // Use LONG for potentially longer messages
            viewModel.onErrorMessageHandled() // Reset the error message
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackgroundGradient) // Apply gradient
    ) {
        // Add Back Button
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.padding(start = 4.dp, top = 36.dp) // Adjust padding as needed
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()), // Make column scrollable
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Lets get you set up!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextColorPrimary
            )
            Spacer(modifier = Modifier.height(24.dp))

            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Setup Icon",
                modifier = Modifier.size(80.dp),
                tint = TextColorPrimary
            )
            Spacer(modifier = Modifier.height(24.dp))

            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppPrimaryColor,
                unfocusedBorderColor = Color.Gray,
                cursorColor = AppPrimaryColor,
                focusedTextColor = TextFieldTextColor,
                unfocusedTextColor = TextFieldTextColor,
                focusedContainerColor = TextFieldBackgroundColor,
                unfocusedContainerColor = TextFieldBackgroundColor,
                focusedLabelColor = TextFieldPlaceholderColor,
                unfocusedLabelColor = TextFieldPlaceholderColor
            )

            OutlinedTextField(
                value = uiState.username,
                onValueChange = viewModel::onUsernameChanged,
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = textFieldColors,
                singleLine = true,
                enabled = !uiState.isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChanged,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = textFieldColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                enabled = !uiState.isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.confirmEmail,
                onValueChange = viewModel::onConfirmEmailChanged,
                label = { Text("Confirm Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = textFieldColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                enabled = !uiState.isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChanged,
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(8.dp),
                colors = textFieldColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                enabled = !uiState.isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChanged,
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(8.dp),
                colors = textFieldColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                enabled = !uiState.isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.phoneNumber,
                onValueChange = viewModel::onPhoneNumberChanged,
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = textFieldColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                enabled = !uiState.isLoading
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = viewModel::onSignUpClicked,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppPrimaryColor),
                enabled = !uiState.isLoading
            ) {
                 if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 3.dp
                    )
                } else {
                     Text("Create new Account", color = Color.White, fontSize = 18.sp)
                 }
            }
            Spacer(modifier = Modifier.height(32.dp)) // Add space at the bottom
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun SignUpScreenPreview() {
    SIT305_61DTheme {
        SignUpScreen({}, {}) // Preview won't use the ViewModel
    }
}
