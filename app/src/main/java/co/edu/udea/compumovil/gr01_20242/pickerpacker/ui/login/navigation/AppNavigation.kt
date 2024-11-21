package co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.ui.ForgotPasswordScreen
import co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.ui.ForgotPasswordViewModel
import co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.ui.LoginScreen
import co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.ui.LoginViewModel
import co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.ui.RegisterScreen
import co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.ui.RegisterViewModel
import co.edu.udea.compumovil.gr01_20242.pickerpacker.uiNavegation.NavigationScreen
import com.google.firebase.auth.FirebaseUser


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val loginViewModel = LoginViewModel()
    val user: FirebaseUser? = LoginViewModel().getCurrentUser()

    NavHost(
        navController,
        startDestination = if (user == null) AppScreens.LoginScreen.route else AppScreens.MenuScreen.route
    ) {
        composable(AppScreens.LoginScreen.route) {
            LoginScreen(
                loginViewModel,
                navController)
        }
        composable(AppScreens.RegisterScreen.route) {
            RegisterScreen(
                RegisterViewModel(),
                navController
            )
        }
        composable(AppScreens.ForgotPasswordScreen.route) {
            ForgotPasswordScreen(
                ForgotPasswordViewModel(),
                navController
            )
        }
        composable(AppScreens.MenuScreen.route) {
            NavigationScreen(
                navController,
                loginViewModel
            )
        }
    }
}


