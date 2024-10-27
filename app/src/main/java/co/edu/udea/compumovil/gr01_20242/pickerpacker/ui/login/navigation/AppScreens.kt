package co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.navigation

sealed class AppScreens (val route: String) {

    object LoginScreen : AppScreens("login_screen")
    object RegisterScreen : AppScreens("register_screen")
    object MenuScreen : AppScreens("menu_screen")


}