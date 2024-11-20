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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


@Composable
fun Herramientas() {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageProcesadaUri by remember { mutableStateOf<Uri?>(null) }
    var isImageExpanded by remember { mutableStateOf(false) } // Estado para controlar si la imagen está ampliada
    var expandedImageUri by remember { mutableStateOf<Uri?>(null) } // Estado para guardar la URI de la imagen que se amplía
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) } // bitmap que va a ser usado para procesar la imagen
    var bitmap2 by remember { mutableStateOf<Bitmap?>(null) }

    // URI de la imagen predeterminada
    var defaultImageUri = remember { Uri.parse("android.resource://${context.packageName}/drawable/imagen_base") }
    val procesadaImageUri = remember { Uri.parse("android.resource://${context.packageName}/drawable/imagen_base") }

    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)


    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            bitmap = result.data?.extras?.get("data") as? Bitmap


            // Guardar la imagen en un archivo temporal
            bitmap?.let { bitmap ->
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
        bitmap = uri?.let { MediaStore.Images.Media.getBitmap(context.contentResolver, it) }
        //val source = createSource(context.contentResolver, uri!!)
        //bitmap = decodeBitmap(source)

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
            Button(onClick = { // limpiar la pantalla
                defaultImageUri = null
                imageUri = null
                bitmap = null
                bitmap2 = null
            }
            )
            {
                Text(text = "Limpiar")
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
                    expandedImageUri =
                        imageUri ?: defaultImageUri // Asigna la URI correcta para ampliarla
                    isImageExpanded =
                        true // Al hacer clic en la imagen, cambiar el estado para expandirla
                }

        )

        // mostrar información de la imagen inicial como el tamaño en bytes de la imagen
        imageUri?.let {
            Text(
                text =  "Tamaño de la imagen inicial: "+ getBitmapSizeInBytes(bitmap!!).toString() + " bytes",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Botón Procesar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                // Lógica de procesamiento de imagen (aquí solo se pone un ejemplo de cambiar la imagen)
                //imageProcesadaUri = Uri.parse("android.resource://${context.packageName}/drawable/imagen_procesada_demo") // Aquí pondrías la imagen procesada
                bitmap2 = compressImageUsingQuadTree(bitmap!!)

            }) {
                Text(text = "Procesar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar la imagen procesada.
        val imageprocesadaToShow = imageProcesadaUri ?: procesadaImageUri
        Image(
            painter = rememberAsyncImagePainter(bitmap2),
            contentDescription = "Foto procesada",

            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(fraction = 1f)
                .height(500.dp)
                .clickable {
                    expandedImageUri = imageProcesadaUri
                        ?: procesadaImageUri // Asigna la URI correcta para ampliarla
                    isImageExpanded =
                        true // Al hacer clic en la imagen procesada, cambiar el estado para expandirla
                }
        )

        // mostrar información de la imagen procesada como el tamaño en bytes de la imagen
        bitmap2?.let {
            Text(
                text =  "Tamaño de la imagen procesada: "+ getBitmapSizeInBytes(bitmap2!!).toString() + " bytes",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                )
            }
        }
    }
    // función que permite limpiar la pantalla
    fun limpiar(){
        defaultImageUri = null
        imageUri = null
        bitmap = null
        bitmap2 = null
    }
}
// función que obtienen el tamaño de la imagen en bytes
private fun getBitmapSizeInBytes(bitmap: Bitmap): Int {
    val byteStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream)
    val byteArray = byteStream.toByteArray()
    return byteArray.size
}



// QuadTree implementation
data class QuadTreeNode(
    val x: Int, val y: Int, val size: Int,
    val color: Int? = null, val isLeaf: Boolean = false,
    val nw: QuadTreeNode? = null, val ne: QuadTreeNode? = null,
    val sw: QuadTreeNode? = null, val se: QuadTreeNode? = null
)

fun Bitmap.getAverageColor(x: Int, y: Int, size: Int): Int {
    var r = 0
    var g = 0
    var b = 0
    var count = 0

    for (i in x until x + size) {
        for (j in y until y + size) {
            val color = getPixel(i, j)
            r += android.graphics.Color.red(color)
            g += android.graphics.Color.green(color)
            b += android.graphics.Color.blue(color)
            count++
        }
    }

    return android.graphics.Color.rgb(r / count, g / count, b / count)
}

fun Bitmap.isHomogeneous(x: Int, y: Int, size: Int, tolerance: Int): Boolean {
    val averageColor = getAverageColor(x, y, size)
    for (i in x until x + size) {
        for (j in y until y + size) {
            val color = getPixel(i, j)
            if (Math.abs(android.graphics.Color.red(color) - android.graphics.Color.red(averageColor)) > tolerance ||
                Math.abs(android.graphics.Color.green(color) - android.graphics.Color.green(averageColor)) > tolerance ||
                Math.abs(android.graphics.Color.blue(color) - android.graphics.Color.blue(averageColor)) > tolerance) {
                return false
            }
        }
    }
    return true
}

fun buildQuadTree(bitmap: Bitmap, x: Int, y: Int, size: Int, tolerance: Int): QuadTreeNode {
    if (size <= 1 || bitmap.isHomogeneous(x, y, size, tolerance)) {
        return QuadTreeNode(x, y, size, bitmap.getAverageColor(x, y, size), true)
    }

    val halfSize = size / 2
    return QuadTreeNode(
        x, y, size, null, false,
        buildQuadTree(bitmap, x, y, halfSize, tolerance),
        buildQuadTree(bitmap, x + halfSize, y, halfSize, tolerance),
        buildQuadTree(bitmap, x, y + halfSize, halfSize, tolerance),
        buildQuadTree(bitmap, x + halfSize, y + halfSize, halfSize, tolerance)
    )
}

fun drawQuadTree(bitmap: Bitmap, node: QuadTreeNode) {
    val colorTransparente: Int = android.graphics.Color.argb(0, 0, 0, 0) // Transparente
    if (node.isLeaf && node.color != null) {
        for (i in node.x until node.x + node.size) {
            for (j in node.y until node.y + node.size) {
                bitmap.setPixel(i, j, node.color)
            }
        }
    }
    else {
        node.nw?.let { drawQuadTree(bitmap, it) }
        node.ne?.let { drawQuadTree(bitmap, it) }
        node.sw?.let { drawQuadTree(bitmap, it) }
        node.se?.let { drawQuadTree(bitmap, it) }
    }
}

fun compressImageUsingQuadTree(bitmap: Bitmap): Bitmap {
    val width = bitmap.width
    val height = bitmap.height


    // Crear una nueva imagen bitmap para almacenar la imagen comprimida
    val compressedBitmap = Bitmap.createBitmap(width, height, bitmap.config!!)

    // Generar el quadtree y dibujar la imagen comprimida
    val quadtree = buildQuadTree(bitmap, 0, 0, width.coerceAtMost(height), 1)
    drawQuadTree(compressedBitmap, quadtree)
    return compressedBitmap
}
