package co.edu.udea.compumovil.gr01_20242.pickerpacker.uiNavegation

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class NavigationViewModel : ViewModel() {
    var currentScreen = mutableStateOf<NavigationUiState>(NavigationUiState.Herramientas)
    var selectedImageUri = mutableStateOf<Uri?>(null)
    var capturedImageUri = mutableStateOf<Uri?>(null)

    fun navigateTo(screen: NavigationUiState) {
        currentScreen.value = screen
    }

}