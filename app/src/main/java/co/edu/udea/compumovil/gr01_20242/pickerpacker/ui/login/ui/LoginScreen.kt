package co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import co.edu.udea.compumovil.gr01_20242.pickerpacker.R
import co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.navigation.AppScreens

@Composable
fun LoginScreen(viewModel: LoginViewModel, navController: NavController) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Login(Modifier.align(Alignment.Center), viewModel, navController)
    }
}

@Composable
private fun Login(
    modifier: Modifier,
    viewModel: LoginViewModel,
    navController: NavController
) {
    val email: String by viewModel.email.observeAsState(initial = "")
    val password: String by viewModel.password.observeAsState(initial = "")
    val loginEnable: Boolean by viewModel.loginEnable.observeAsState(initial = false)
    val isLoading: Boolean by viewModel.isLoading.observeAsState(initial = false)
    val loginResult: Boolean? by viewModel.loginResult.observeAsState()
    val errorMessage: String by viewModel.errorMessage.observeAsState("")
    val coroutineScope = rememberCoroutineScope()

    if (isLoading) {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    } else {
        Column(modifier = modifier) {
            HeaderImage(Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.padding(32.dp))
            EmailField(email) { viewModel.onLoginChanged(it, password) }
            Spacer(modifier = Modifier.padding(4.dp))
            PasswordField(password) { viewModel.onLoginChanged(email, it) }
            Spacer(modifier = Modifier.padding(8.dp))
            ForgotPassword(Modifier.align(Alignment.End), navController)
            Spacer(modifier = Modifier.padding(16.dp))
            LoginButton(loginEnable) {
                coroutineScope.launch {
                    viewModel.onLoginSelected()
                }
            }
            Spacer(modifier = Modifier.padding(16.dp))
            LoginStatusMessage(loginResult, errorMessage, navController)

            // Texto para registro
            Spacer(modifier = Modifier.padding(8.dp))
            RegisterText(Modifier.align(Alignment.CenterHorizontally), navController)
        }
    }
}

@Composable
private fun RegisterText(modifier: Modifier, navController: NavController) {
    Text(
        text = "¿Todavía no tienes cuenta? Regístrate",
        modifier = modifier
            .fillMaxWidth()
            .clickable { navController.navigate(AppScreens.RegisterScreen.route) }
            .padding(vertical = 8.dp),
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF49a63a),
        textAlign = TextAlign.Center
    )
}


@Composable
private fun LoginStatusMessage(
    loginResult: Boolean?,
    errorMessage: String,
    navController: NavController
) {
    when (loginResult) {
        true -> {
            LaunchedEffect(Unit) {
               navController.navigate(AppScreens.MenuScreen.route)
            }
        }
        false -> {
            // Envolvemos el Text en una Box para poder centrarlo horizontalmente
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center // Alinea horizontalmente el texto
            ) {
                Text(
                    text = errorMessage.ifEmpty { "Login failed" },
                    color = Color.Red
                )
            }
        }
        null -> Unit // No mostrar nada si loginResult es null
    }
}

@Composable
private fun LoginButton(loginEnable: Boolean, onLoginSelected: () -> Unit) {
    Button(
        onClick = { onLoginSelected() },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (loginEnable) Color(0xFF78C800) else Color(0xFFBDBDBD),
            disabledContainerColor = Color(0xFFBDBDBD),
            contentColor = Color.White,
            disabledContentColor = Color.White
        ),
        enabled = loginEnable
    ) {
        Text(text = "Iniciar Sesión")
    }
}

@Composable
private fun ForgotPassword(modifier: Modifier, navController: NavController) {
    Text(
        text = "Olvidaste la contraseña?",
        modifier = modifier.clickable {  },
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF49a63a)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordField(password: String, onTextFieldChanged: (String) -> Unit) {
    TextField(
        value = password,
        onValueChange = { onTextFieldChanged(it) },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFDEDDDD), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text(text = "Contraseña", color = Color(0xFF636262)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        maxLines = 1
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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
        painter = painterResource(R.drawable.login_udea),
        contentDescription = "Logo_Login",
        modifier = modifier
    )
}


