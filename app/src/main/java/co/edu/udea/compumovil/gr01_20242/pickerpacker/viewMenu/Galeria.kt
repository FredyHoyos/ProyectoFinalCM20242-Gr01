package co.edu.udea.compumovil.gr01_20242.pickerpacker.viewMenu

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import java.io.File

@Composable
fun Galeria() {
    val context = LocalContext.current
    val savedImages = remember { getSavedImages(context) }  // Obtener la lista de imágenes guardadas
    var selectedImage by remember { mutableStateOf<File?>(null) }  // Imagen seleccionada para ampliación

    Box(modifier = Modifier.fillMaxSize()) {
        // Galería de imágenes en cuadrícula
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),  // Definir dos columnas
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(savedImages) { imageFile ->
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)

                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Imagen guardada",
                        modifier = Modifier
                            .padding(8.dp)
                            .aspectRatio(1f)  // Mantener la relación de aspecto
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = 2.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .fillMaxWidth()
                            .clickable {
                                selectedImage = imageFile  // Establecer la imagen seleccionada
                            }
                    )
                }
            }
        }

        // Mostrar imagen ampliada cuando se selecciona
        selectedImage?.let { imageFile ->
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)

            bitmap?.let {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)  // Fondo oscuro sólido
                        .zIndex(1f)  // Asegurarse de que la imagen ampliada esté encima de la cuadrícula
                        .clickable {
                            selectedImage = null  // Limpiar la selección cuando se hace clic fuera
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Imagen ampliada",
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .fillMaxWidth()
                            .aspectRatio(1f)  // Mantener la relación de aspecto
                            .border(
                                width = 2.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(16.dp)
                            )
                    )
                }
            }
        }
    }
}

// Función para obtener las imágenes guardadas en el almacenamiento local
fun getSavedImages(context: Context): List<File> {
    val filesDir = context.filesDir
    return filesDir.listFiles { file -> file.extension == "png" }?.toList() ?: emptyList()
}
