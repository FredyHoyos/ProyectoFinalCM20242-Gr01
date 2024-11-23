package co.edu.udea.compumovil.gr01_20242.pickerpacker.viewMenu

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Handler
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
    var savedImages = remember { getSavedImages(context) }  // Obtener la lista de imágenes guardadas
    var selectedImage by remember { mutableStateOf<File?>(null) }  // Imagen seleccionada para ampliación
    var showSnackbar by remember { mutableStateOf(false) }  // Control para mostrar el mensaje emergente
    var currentImage by remember { mutableStateOf<File?>(null) }  // Control de la imagen actual que está siendo ampliada

    Box(modifier = Modifier.fillMaxSize()) {
        // Galería de imágenes en cuadrícula
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),  // Definir dos columnas
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color(0xFFF0F4F8))
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
                                currentImage = imageFile  // Imagen actual para eliminar
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Imagen ampliada
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

                        // Botón de eliminación
                        Spacer(modifier = Modifier.height(16.dp))
                        ElevatedButton(
                            onClick = {
                                currentImage?.let { image ->
                                    deleteImage(context, image)  // Eliminar imagen del almacenamiento
                                    showSnackbar = true  // Mostrar mensaje emergente
                                    // Actualizar la lista de imágenes después de eliminar
                                    savedImages = getSavedImages(context)
                                    selectedImage = null  // Limpiar la imagen seleccionada
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xfff44336)) // Rojo
                        ) {
                            Text("Eliminar Imagen")
                        }
                    }
                }
            }
        }

        // Mostrar mensaje emergente (Snackbar)
        if (showSnackbar) {
            Snackbar(
                action = {
                    Button(onClick = { showSnackbar = false },
                           colors = ButtonDefaults.buttonColors(containerColor = Color(0xff57d159))
                    ) {
                        Text("Cerrar")
                    }
                },
                modifier = Modifier.padding(16.dp),
                containerColor = Color(0xFF8de88a)
            ) {
                Text("Imagen eliminada")
            }
        }
    }
}

// Función para eliminar la imagen
fun deleteImage(context: Context, imageFile: File) {
    if (imageFile.exists()) {
        val isDeleted = imageFile.delete()  // Eliminar archivo de almacenamiento
        if (isDeleted) {
            Toast.makeText(context, "Imagen eliminada", Toast.LENGTH_SHORT).show()
        }
    }
}

// Función para obtener las imágenes guardadas en el almacenamiento local
fun getSavedImages(context: Context): List<File> {
    val filesDir = context.filesDir
    return filesDir.listFiles { file -> file.extension == "png" }?.toList() ?: emptyList()
}
