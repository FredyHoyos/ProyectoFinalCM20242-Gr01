package co.edu.udea.compumovil.gr01_20242.pickerpacker.viewMenu


import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.udea.compumovil.gr01_20242.pickerpacker.uiNavegation.NavigationViewModel
import coil.compose.rememberAsyncImagePainter

@Composable
fun Galeria(navigationViewModel: NavigationViewModel = viewModel()) {
    val context = LocalContext.current
    val selectedImageUri = navigationViewModel.selectedImageUri.value

    // Crea el launcher para seleccionar una imagen
    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                navigationViewModel.selectedImageUri.value = uri // Almacena la URI en el ViewModel
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    )

    Column {
        // Botón para lanzar el selector de medios
        Button(onClick = {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }) {
            Text("Seleccionar Imagen")
        }

        // Muestra la imagen seleccionada, si existe
        selectedImageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Imagen seleccionada",
                modifier = Modifier.fillMaxWidth() // Ajusta según tus necesidades
            )
        }
    }
}
