package co.edu.udea.compumovil.gr01_20242.pickerpacker.uiNavegation

sealed class NavigationUiState {
    object Informacion : NavigationUiState()
    object Herramientas : NavigationUiState()
    object Galeria : NavigationUiState()
    object Salir : NavigationUiState()
}