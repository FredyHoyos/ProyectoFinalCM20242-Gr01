package co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.edu.udea.compumovil.gr01_20242.pickerpacker.R
import co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.navigation.AppScreens
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation



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
    val token = "965750021284-0khjd91uqt9rft51pkv988uabnst5eeh.apps.googleusercontent.com"
    val context = LocalContext.current
    val errorMessage: String by viewModel.errorMessage.observeAsState("")
    val coroutineScope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ){
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val account = task.getResult(java.lang.Exception::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            viewModel.startGoogleLogin(credential) {
                    navController.navigate(AppScreens.MenuScreen.route)
            }
        }catch (e: Exception){
            Log.d("GoogleLoginError", "startGooglelogin failed:")
        }
    }
    if (isLoading) {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    } else {
        Column(modifier = modifier) {
            HeaderImage(Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.padding(16.dp))
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
            Spacer(modifier = Modifier.padding(8.dp))
            LoginStatusMessage(loginResult, errorMessage, navController)
            Spacer(modifier = Modifier.padding(8.dp))
            Separator(Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.padding(8.dp))
            GoogleloginButton(token, context, launcher)
            Spacer(modifier = Modifier.padding(8.dp))
            RegisterText(Modifier.align(Alignment.CenterHorizontally), navController)
        }
    }
}

@Composable
private fun GoogleloginButton(token: String, context: Context, launcher: ActivityResultLauncher<Intent>) {
    OutlinedButton(
        onClick = {
                    val opciones = GoogleSignInOptions.Builder(
                        GoogleSignInOptions.DEFAULT_SIGN_IN
                    )
                        .requestIdToken(token)
                        .requestEmail()
                        .build()
                        val googleSignInClient = GoogleSignIn.getClient(context, opciones)
                        launcher.launch(googleSignInClient.signInIntent)
                  },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF1F1F1),
            contentColor = Color.Black
        )
    ) {
        Image(
            painterResource(id = R.drawable.ic_google),
            contentDescription = "Login con Google",
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Iniciar Sesión con Google"
        )
    }
}

@Composable
private fun Separator(modifier: Modifier) {
    Text(
        text = "----------- o -----------",
        modifier = modifier,
        fontSize = 14.sp,
        color = Color(0xFF49a63a)
    )
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
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage.ifEmpty { "Login failed" },
                    color = Color.Red
                )
            }
        }
        null -> Unit
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
        modifier = modifier.clickable {
            navController.navigate(AppScreens.ForgotPasswordScreen.route)
        },
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF49a63a)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordField(password: String, onTextFieldChanged: (String) -> Unit) {
    // Estado para controlar la visibilidad de la contraseña
    var passwordVisible by remember { mutableStateOf(false) }

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
        maxLines = 1,
        // Cambia la visualización según si la contraseña es visible o no
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            // Ícono de "ojo" para mostrar/ocultar contraseña
            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña")
            }
        }
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
        modifier = modifier.size(300.dp, 300.dp)
    )
}
