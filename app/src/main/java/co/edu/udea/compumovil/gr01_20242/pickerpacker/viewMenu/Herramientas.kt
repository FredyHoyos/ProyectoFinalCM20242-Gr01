package co.edu.udea.compumovil.gr01_20242.pickerpacker.viewMenu

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import java.io.File
import java.io.FileOutputStream

@Composable
fun Herramientas() {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageProcesadaUri by remember { mutableStateOf<Uri?>(null) }
    var isImageExpanded by remember { mutableStateOf(false) } // Estado para controlar si la imagen está ampliada
    var expandedImageUri by remember { mutableStateOf<Uri?>(null) } // Estado para guardar la URI de la imagen que se amplía
    val context = LocalContext.current

    // URI de la imagen predeterminada
    val defaultImageUri = remember { Uri.parse("android.resource://${context.packageName}/drawable/imagen_base") }
    val procesadaImageUri = remember { Uri.parse("android.resource://${context.packageName}/drawable/imagen_base") }

    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

    // Launcher para la cámara
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            // Guardar la imagen en un archivo temporal
            imageBitmap?.let { bitmap ->
                val file = File(context.cacheDir, "temp_image.jpg")
                try {
                    FileOutputStream(file).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    }
                    imageUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Launcher para seleccionar imagen de la galería
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    // Contenedor de Scroll
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()) // Hacer scroll vertical
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = { cameraLauncher.launch(intent) }) {
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

        // Mostrar la imagen tomada o subida, o la imagen predeterminada
        val imageToShow = imageUri ?: defaultImageUri
        Image(
            painter = rememberAsyncImagePainter(imageToShow),
            contentDescription = "Foto tomada o imagen predeterminada",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clickable {
                    expandedImageUri = imageUri ?: defaultImageUri // Asigna la URI correcta para ampliarla
                    isImageExpanded = true // Al hacer clic en la imagen, cambiar el estado para expandirla
                }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Procesar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                // Lógica de procesamiento de imagen (aquí solo se pone un ejemplo de cambiar la imagen)
                imageProcesadaUri = Uri.parse("android.resource://${context.packageName}/drawable/imagen_procesada_demo") // Aquí pondrías la imagen procesada
            }) {
                Text(text = "Procesar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar la imagen procesada.
        val imageprocesadaToShow = imageProcesadaUri ?: procesadaImageUri
        Image(
            painter = rememberAsyncImagePainter(imageprocesadaToShow),
            contentDescription = "Foto procesada",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clickable {
                    expandedImageUri = imageProcesadaUri ?: procesadaImageUri // Asigna la URI correcta para ampliarla
                    isImageExpanded = true // Al hacer clic en la imagen procesada, cambiar el estado para expandirla
                }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Guardar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                // Lógica para guardar la imagen procesada
            }) {
                Text(text = "Guardar")
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
    }

    // Dialog para mostrar la imagen ampliada cuando se hace clic
    if (isImageExpanded) {
        Dialog(onDismissRequest = { isImageExpanded = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                // Mostrar la imagen correspondiente dependiendo de cuál fue clickeada
                Image(
                    painter = rememberAsyncImagePainter(expandedImageUri),
                    contentDescription = "Imagen ampliada",
                    modifier = Modifier.fillMaxWidth().fillMaxHeight()
                )
            }
        }
    }
}
