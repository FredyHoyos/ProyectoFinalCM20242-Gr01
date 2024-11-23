package co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.edu.udea.compumovil.gr01_20242.pickerpacker.R
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(viewModel: RegisterViewModel, navController: NavController) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF0F4F8))
    ) {
        Register(Modifier.align(Alignment.Center), viewModel, navController)
    }
}

@Composable
private fun Register(
    modifier: Modifier,
    viewModel: RegisterViewModel,
    navController: NavController
) {
    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")
    val registerEnable by viewModel.registerEnable.observeAsState(false)
    val isLoading by viewModel.isLoading.observeAsState(false)
    val registerResult by viewModel.registerResult.observeAsState(null)
    val errorMessage by viewModel.errorMessage.observeAsState("")
    val coroutineScope = rememberCoroutineScope()

    if (isLoading) {
        Box(Modifier.fillMaxSize().background(Color(0xFFF0F4F8))) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    } else {
        Column(modifier = modifier) {
            HeaderImage(Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.padding(32.dp))
            EmailField(email) { viewModel.onRegisterChanged(it, password) }
            Spacer(modifier = Modifier.padding(4.dp))
            PasswordField(password) { viewModel.onRegisterChanged(email, it) }
            Spacer(modifier = Modifier.padding(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BackToLoginButton(navController)
                RegisterButton(registerEnable) {
                    coroutineScope.launch {
                        viewModel.onRegisterSelected()
                    }
                }
            }

            Spacer(modifier = Modifier.padding(16.dp))
            RegistrationStatus(registerResult, errorMessage)
        }
    }
}

@Composable
private fun BackToLoginButton(navController: NavController) {
    Button(
        onClick = { navController.popBackStack()  },
        modifier = Modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.LightGray,
            contentColor = Color.Black
        )
    ) {
        Text(text = "Back to Login")
    }
}

@Composable
private fun RegistrationStatus(
    registerResult: Boolean?,
    errorMessage: String
) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        when (registerResult) {
            true -> {
                Text(
                    text = "Registration successful!",
                    color = Color.Green
                )
            }
            false -> {
                Text(
                    text = errorMessage.ifEmpty { "Registration failed" },
                    color = Color.Red
                )
            }
            null -> {}
        }
    }
}

@Composable
private fun RegisterButton(registerEnable: Boolean, onRegisterSelected: () -> Unit) {
    Button(
        onClick = { onRegisterSelected() },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF78C800),
            disabledContainerColor = Color(0xFF9bcd61),
            contentColor = Color.White,
            disabledContentColor = Color.White
        ),
        enabled = registerEnable
    ) {
        Text(text = "Register")
    }
}

@Composable
private fun PasswordField(password: String, onTextFieldChanged: (String) -> Unit) {
    TextField(
        value = password,
        onValueChange = { onTextFieldChanged(it) },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFDEDDDD), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text(text = "Password", color = Color(0xFF636262)) },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        maxLines = 1
    )
}

@Composable
private fun EmailField(email: String, onTextFieldChanged: (String) -> Unit) {
    TextField(
        value = email,
        onValueChange = { onTextFieldChanged(it) },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFDEDDDD), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text(text = "Email", color = Color(0xFF636262)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        maxLines = 1
    )
}

@Composable
private fun HeaderImage(modifier: Modifier) {
    Image(
        painter = painterResource(R.drawable.ico_register),
        contentDescription = "Register Logo",
        modifier = modifier
    )
}

