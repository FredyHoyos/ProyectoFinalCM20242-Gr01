package co.edu.udea.compumovil.gr01_20242.pickerpacker.viewMenu


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import co.edu.udea.compumovil.gr01_20242.pickerpacker.R
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream


@Composable
fun Herramientas() {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var bitmap2 by remember { mutableStateOf<Bitmap?>(null) }
    val scope = rememberCoroutineScope()

    // Cargar la imagen predeterminada al iniciar
    LaunchedEffect(Unit) {
        bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.imagen_base)
        bitmap2 = bitmap
    }

    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            bitmap = result.data?.extras?.get("data") as? Bitmap
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }

            context.contentResolver.openInputStream(it)?.let { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, options)
                options.inSampleSize = 4  // Ajusta el factor de muestra para reducir la resolución
                options.inJustDecodeBounds = false
            }

            context.contentResolver.openInputStream(it)?.let { inputStream ->
                val bitmap1 = BitmapFactory.decodeStream(inputStream, null, options)
                bitmap = bitmap1
            }
        }
    }

    // Contenedor de Scroll
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Fila de botones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { cameraLauncher.launch(intent) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) { Text("Tomar Foto") }

            Button(
                onClick = { galleryLauncher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) { Text("Subir Foto") }

            Button(
                onClick = {
                    bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.imagen_base)
                    bitmap2 = bitmap
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) { Text("Limpiar") }
        }

        // Imagen principal
        bitmap?.let {
            val aspectRatio = it.width.toFloat() / it.height.toFloat()
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Imagen tomada o predeterminada",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio)  // Mantener la relación de aspecto
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(16.dp))
                    .padding(8.dp)
            )
        }

        bitmap?.let {
            Text(
                text = "Tamaño de la imagen inicial: ${getBitmapSizeInBytes(it)} bytes",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        // Botón de procesamiento
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            ElevatedButton(
                onClick = {
                    scope.launch {
                        bitmap2 = compressImageUsingQuadTreeAsync(bitmap!!)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) { Text("Procesar Imagen") }
        }

        // Imagen procesada
        bitmap2?.let {
            val aspectRatio = it.width.toFloat() / it.height.toFloat()
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Imagen procesada",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio)  // Mantener la relación de aspecto
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(16.dp))
                    .padding(8.dp)
            )
        }

        bitmap2?.let {
            Text(
                text = "Tamaño de la imagen procesada: ${getBitmapSizeInBytes(it)} bytes",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        // Botón para guardar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            ElevatedButton(
                onClick = { /* Lógica para guardar la imagen procesada */ },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) { Text("Guardar Imagen") }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}


// Función que obtiene el tamaño de la imagen en bytes
private fun getBitmapSizeInBytes(bitmap: Bitmap): Int {
    val byteStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream)
    return byteStream.toByteArray().size
}

// Función para comprimir la imagen utilizando QuadTree de forma asíncrona
suspend fun compressImageUsingQuadTreeAsync(bitmap: Bitmap): Bitmap {
    return withContext(Dispatchers.Default) {  // Usamos Dispatchers.Default para mover la operación a un hilo de fondo
        compressImageUsingQuadTree(bitmap)  // Comprimir la imagen
    }
}

// Función para comprimir la imagen utilizando QuadTree
fun compressImageUsingQuadTree(bitmap: Bitmap): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val compressedBitmap = Bitmap.createBitmap(width, height, bitmap.config!!)
    val quadtree = buildQuadTree(bitmap, 0, 0, width.coerceAtMost(height), 1)
    drawQuadTree(compressedBitmap, quadtree)
    return compressedBitmap
}

// Implementación de QuadTree
data class QuadTreeNode(
    val x: Int, val y: Int, val size: Int,
    val color: Int? = null, val isLeaf: Boolean = false,
    val nw: QuadTreeNode? = null, val ne: QuadTreeNode? = null,
    val sw: QuadTreeNode? = null, val se: QuadTreeNode? = null
)

// Función que obtiene el color promedio en un área de la imagen
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
    if (node.isLeaf && node.color != null) {
        for (i in node.x until node.x + node.size) {
            for (j in node.y until node.y + node.size) {
                bitmap.setPixel(i, j, node.color)
            }
        }
    } else {
        node.nw?.let { drawQuadTree(bitmap, it) }
        node.ne?.let { drawQuadTree(bitmap, it) }
        node.sw?.let { drawQuadTree(bitmap, it) }
        node.se?.let { drawQuadTree(bitmap, it) }
    }
}