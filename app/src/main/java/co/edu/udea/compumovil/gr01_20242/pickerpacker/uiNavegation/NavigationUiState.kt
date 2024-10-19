package co.edu.udea.compumovil.gr01_20242.pickerpacker.uiNavegation

sealed class NavigationUiState {
    object ShowUserData : NavigationUiState()
    object Herramientas : NavigationUiState()
    object Informacion : NavigationUiState()
    object Galeria : NavigationUiState()
    object Salir : NavigationUiState()
    object TakePhoto : NavigationUiState()
    object OpenGallery : NavigationUiState()

}