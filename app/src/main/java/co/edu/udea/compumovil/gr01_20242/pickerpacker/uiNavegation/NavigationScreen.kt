package co.edu.udea.compumovil.gr01_20242.pickerpacker.uiNavegation

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.udea.compumovil.gr01_20242.pickerpacker.R
import co.edu.udea.compumovil.gr01_20242.pickerpacker.viewMenu.Camara
import co.edu.udea.compumovil.gr01_20242.pickerpacker.viewMenu.DatosUsuario
import co.edu.udea.compumovil.gr01_20242.pickerpacker.viewMenu.Galeria
import co.edu.udea.compumovil.gr01_20242.pickerpacker.viewMenu.Herramientas
import co.edu.udea.compumovil.gr01_20242.pickerpacker.viewMenu.Salir


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationScreen() {
    val navigationViewModel: NavigationViewModel = viewModel()
    val currentScreen = navigationViewModel.currentScreen.value

    Column {
        TopAppBar(
            title = {},
            actions = {
                // Usamos un Row para alinear los íconos
                Row(
                    modifier = Modifier
                        .background(Color.Blue)
                        .fillMaxWidth()
                        .weight(1f), // Para ocupar todo el espacio disponible
                    horizontalArrangement = Arrangement.SpaceAround // Espacio entre los íconos
                ) {
                    IconButton(onClick = { navigationViewModel.navigateTo(NavigationUiState.ShowUserData) }) {
                        Image(
                            painter = painterResource(id = R.drawable.ico_perfil), "Usuario",
                            modifier = Modifier.size(30.dp))
                    }
                    IconButton(onClick = { navigationViewModel.navigateTo(NavigationUiState.Informacion) }) {
                        Image(
                            painter = painterResource(id = R.drawable.ico_informacion), contentDescription ="Informacion",
                            modifier = Modifier.size(30.dp))
                    }
                    IconButton(onClick = { navigationViewModel.navigateTo(NavigationUiState.Herramientas) }) {
                        Icon(
                            Icons.Default.Favorite, "Aplicacion",
                            tint = Color.Red,
                            modifier = Modifier.size(30.dp))
                    }
                    IconButton(onClick = { navigationViewModel.navigateTo(NavigationUiState.OpenGallery) }) {
                        Image(
                            painter = painterResource(id = R.drawable.ico_galeria), contentDescription = "Galería",
                            modifier = Modifier.size(30.dp))
                    }
                    IconButton(onClick = { navigationViewModel.navigateTo(NavigationUiState.Salir) }) {
                        Image(
                            painter = painterResource(id = R.drawable.ico_salir), contentDescription = "Salir",
                            modifier = Modifier.size(30.dp))
                    }
                }
            }
        )


        // Contenido según la pantalla actual
        when (currentScreen) {
            is NavigationUiState.ShowUserData -> {
                DatosUsuario()
            }
            is NavigationUiState.Informacion -> {
                Camara()
            }
            is NavigationUiState.Herramientas -> {
                Herramientas()
            }
            is NavigationUiState.Galeria -> {
                Galeria()
            }
            is NavigationUiState.Salir -> {
                Salir()
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


@Composable
fun DropDownMenu1() {
    var expanded by remember { mutableStateOf(false) }
    val contextForToast = LocalContext.current.applicationContext

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "DropdownMenuItem", Modifier.padding(top = 10.dp), fontSize = 20.sp)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 15.dp)
                .wrapContentSize(align = Alignment.TopStart),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = {
                    expanded = true
                }
            ) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "Open Menu"
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Text("¡Suscríbete!")
                    },
                    onClick = {
                        Toast.makeText(contextForToast, "¡Suscrito😎!", Toast.LENGTH_SHORT).show()
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Favorite,
                            contentDescription = null,
                            tint = androidx.compose.ui.graphics.Color.Red
                        )
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("¡Suscribirse!")
                    },
                    onClick = {
                        Toast.makeText(contextForToast, "Suscribir🙏", Toast.LENGTH_SHORT).show()
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Check,
                            contentDescription = null,
                            tint = androidx.compose.ui.graphics.Color.DarkGray
                        )
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("JetpackCompose.pro")
                    },
                    onClick = {
                        Toast.makeText(contextForToast, "JetpackCompose.pro", Toast.LENGTH_SHORT)
                            .show()
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Home,
                            contentDescription = null,
                            tint = androidx.compose.ui.graphics.Color.DarkGray
                        )
                    }
                )
            }
        }
    }
}