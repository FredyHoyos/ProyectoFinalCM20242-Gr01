package co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch


@Composable
fun ForgotPasswordScreen(viewModel: ForgotPasswordViewModel, navController: NavController) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ForgotPassword(Modifier.align(Alignment.Center), viewModel, navController)
    }

}

@Composable
fun ForgotPassword(
       modifier: Modifier,
       viewModel: ForgotPasswordViewModel,
       navController: NavController
) {
    val email: String by viewModel.email.observeAsState("")
    val isLoading: Boolean by viewModel.isLoading.observeAsState(false)
    val emailEnable by viewModel.emailEnable.observeAsState(false)
    val resetResult: Boolean? by viewModel.resetResult.observeAsState()
    val errorMessage: String by viewModel.errorMessage.observeAsState("")
    val coroutineScope = rememberCoroutineScope()

    if (isLoading) {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    } else {

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Olvidó su contraseña",
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                style = TextStyle(fontSize = 30.sp, color = Color.Green)
            )
            Spacer(modifier = Modifier.height(50.dp))
            EmailField(email) { viewModel.onEmailChanged(it) }
            Spacer(modifier = Modifier.height(30.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BackToLoginButton(navController)
                RecoverPasswordButton(emailEnable) {
                    coroutineScope.launch {
                        viewModel.onResetPasswordSelected()
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
            ForgotPasswordStatusMessage(
                resetResult = resetResult,
                errorMessage = errorMessage
            )

        }
    }
}


@Composable
private fun ForgotPasswordStatusMessage(
    resetResult: Boolean?,
    errorMessage: String,
) {
    when (resetResult) {
        true -> {
            Text(
                text = "¡Enlace enviado! Revisa tu correo.",
                color = Color.Green,
                textAlign = TextAlign.Center
            )
        }

        false -> {
            Text(
                text = errorMessage.ifEmpty { "Error al enviar el enlace" },
                color = Color.Red,
                textAlign = TextAlign.Center
            )
        }

        null -> Unit // No mostrar nada si no hay un resultado aún
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
private fun RecoverPasswordButton(emailEnable: Boolean, onResetPasswordSelected: () -> Unit) {
    Button(
        onClick = {  onResetPasswordSelected() },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF78C800),
            disabledContainerColor = Color(0xFF9bcd61),
            contentColor = Color.White,
            disabledContentColor = Color.White
        ),
        enabled = emailEnable
    ) {
        Text(text = "Recuperar contraseña")
    }
}

@Composable
private fun EmailField(email: String, onEmailChanged: (String) -> Unit) {
    TextField(
        value = email,
        onValueChange = { onEmailChanged(it) },
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
