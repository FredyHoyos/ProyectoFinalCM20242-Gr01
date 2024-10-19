package co.edu.udea.compumovil.gr01_20242.pickerpacker.viewMenuimport

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.content.pm.PackageManager // Importa PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter

@Composable
fun Herramientas() {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    // Launcher for camera
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            imageUri = result.data?.data
        }
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }


    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = {cameraLauncher.launch(intent)}) {
                Text(text = "Tomar foto")
            }
            Button(onClick = { galleryLauncher.launch("image/*") }) {
                Text(text = "Subir foto")
            }
            Button(onClick = { /* Acción para Configurar */ }) {
                Text(text = "Configurar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = "Foto tomada",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clickable {
                    // Abrir un diálogo de edición de imagen
                }
        )
    }
}
