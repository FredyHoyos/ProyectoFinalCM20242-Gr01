package co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.ui.LoginScreen
import co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.ui.LoginViewModel
import co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.ui.RegisterScreen
import co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.ui.RegisterViewModel
import co.edu.udea.compumovil.gr01_20242.pickerpacker.uiNavegation.NavigationScreen



@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = AppScreens.LoginScreen.route) {
        composable(AppScreens.LoginScreen.route) { LoginScreen(LoginViewModel(), navController) }
        composable(AppScreens.RegisterScreen.route) { RegisterScreen(RegisterViewModel(), navController) }
        composable(AppScreens.MenuScreen.route) { NavigationScreen(navController) }
    }
}