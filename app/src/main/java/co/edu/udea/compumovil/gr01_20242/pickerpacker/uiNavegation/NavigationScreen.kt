package co.edu.udea.compumovil.gr01_20242.pickerpacker.uiNavegation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import co.edu.udea.compumovil.gr01_20242.pickerpacker.R
import co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.ui.LoginViewModel
import co.edu.udea.compumovil.gr01_20242.pickerpacker.viewMenu.Camara
import co.edu.udea.compumovil.gr01_20242.pickerpacker.viewMenu.DatosUsuario
import co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.navigation.AppScreens
import co.edu.udea.compumovil.gr01_20242.pickerpacker.viewMenu.Galeria
import co.edu.udea.compumovil.gr01_20242.pickerpacker.viewMenu.Informacion
import co.edu.udea.compumovil.gr01_20242.pickerpacker.viewMenu.Herramientas
import co.edu.udea.compumovil.gr01_20242.pickerpacker.viewMenu.Salir

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun NavigationScreen(navController: NavController, loginViewModel: LoginViewModel) {
    val navigationViewModel: NavigationViewModel = viewModel()
    val currentScreen = navigationViewModel.currentScreen.value


    Column {
        TopAppBar(
            title = {},
            actions = {
                Row(
                    modifier = Modifier
                        .background(Color.Blue)
                        .fillMaxWidth()
                        .weight(1f), // Para ocupar todo el espacio disponible
                    horizontalArrangement = Arrangement.SpaceAround // Espacio entre los íconos
                ) {
                    // Botón para la pantalla de Información
                    IconButton(
                        onClick = { navigationViewModel.navigateTo(NavigationUiState.Informacion) },
                        modifier = Modifier
                            .background(if (currentScreen is NavigationUiState.Informacion) Color.Gray else Color.Transparent)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ico_informacion),
                            contentDescription = "Informacion",
                            modifier = Modifier.size(30.dp),
                        )
                    }

                    // Botón para la pantalla de Herramientas
                    IconButton(
                        onClick = { navigationViewModel.navigateTo(NavigationUiState.Herramientas) },
                        modifier = Modifier
                            .background(if (currentScreen is NavigationUiState.Herramientas) Color.Gray else Color.Transparent)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ico_camara),
                            contentDescription = "Herramientas",
                            modifier = Modifier.size(30.dp),
                        )
                    }

                    // Botón para la pantalla de Galería
                    IconButton(
                        onClick = { navigationViewModel.navigateTo(NavigationUiState.Galeria) },
                        modifier = Modifier
                            .background(if (currentScreen is NavigationUiState.Galeria) Color.Gray else Color.Transparent)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ico_galeria),
                            contentDescription = "Galería",
                            modifier = Modifier.size(30.dp),
                        )
                    }

                    // Botón para la pantalla de Salir
                    IconButton(
                        onClick = {
                            navController.navigate(AppScreens.LoginScreen.route) {
                                // Limpiar el stack de navegación para que no se pueda volver atrás al menú
                                popUpTo(AppScreens.MenuScreen.route) { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .background(if (currentScreen is NavigationUiState.Salir) Color.Gray else Color.Transparent)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ico_salir),
                            contentDescription = "Salir",
                            modifier = Modifier.size(30.dp),
                        )
                    }
                }
            }
        )

        // Contenido según la pantalla actual
        when (currentScreen) {
            is NavigationUiState.Informacion -> {
                Informacion()
            }
            is NavigationUiState.Herramientas -> {
                Herramientas()
            }
            is NavigationUiState.Galeria -> {
                Galeria()
            }

            is NavigationUiState.Salir -> {
                val applicationContext = LocalContext.current.applicationContext
                Salir(loginViewModel, navController,applicationContext)
            }
            is NavigationUiState.TakePhoto -> {
                DatosUsuario()
            }
            is NavigationUiState.OpenGallery -> {
                Galeria()
            }
        }
    }
}
