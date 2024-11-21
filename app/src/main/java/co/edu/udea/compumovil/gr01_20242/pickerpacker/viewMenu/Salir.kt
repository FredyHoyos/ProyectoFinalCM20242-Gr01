package co.edu.udea.compumovil.gr01_20242.pickerpacker.viewMenu


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.navigation.AppScreens
import co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.ui.LoginViewModel


@Composable
fun Salir(viewModel: LoginViewModel,
            navController: NavController,
            applicationContext: android.content.Context
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.signOut(applicationContext)
        navController.navigate(AppScreens.LoginScreen.route) {
            popUpTo(AppScreens.MenuScreen.route) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }
}