package co.edu.udea.compumovil.gr01_20242.pickerpacker.uiNavegation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class NavigationViewModel : ViewModel() {
    var currentScreen = mutableStateOf<NavigationUiState>(NavigationUiState.ShowUserData)

    fun navigateTo(screen: NavigationUiState) {
        currentScreen.value = screen
    }
}