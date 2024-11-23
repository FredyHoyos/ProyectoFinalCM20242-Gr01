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
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import androidx.compose.material3.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import java.io.File
import java.io.FileOutputStream

@Composable
fun Herramientas() {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var bitmap2 by remember { mutableStateOf<Bitmap?>(null) }
    val scope = rememberCoroutineScope()
    var sliderValue by remember { mutableFloatStateOf(0f) }
    var threshold by remember { mutableIntStateOf(0) }
    var bitmapSize by remember { mutableIntStateOf(0) }
    var showAmpliar by remember { mutableStateOf(false) }  // Estado para mostrar o esconder la imagen ampliada

    if (showAmpliar) {
        Ampliar(bitmap2) { showAmpliar = false }  // Pasar el callback para cerrar la imagen ampliada
    }

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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xff57d159))
            ) { Text("Tomar Foto") }

            Button(
                onClick = { galleryLauncher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xff57d159))
            ) { Text("Subir Foto") }

            Button(
                onClick = {
                    bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.imagen_base)
                    bitmap2 = bitmap
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xff57d159))
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
                    .aspectRatio(aspectRatio)
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "threshold: ${sliderValue.toInt()}")
            Spacer(modifier = Modifier.height(16.dp))
            Slider(
                value = sliderValue,
                onValueChange = {
                    sliderValue = it
                    threshold = sliderValue.toInt()
                    bitmap2 = compressImageUsingQuadTree(bitmap!!, threshold)
                    bitmapSize = getBitmapSizeInBytes(bitmap2!!)
                },
                colors = SliderDefaults.colors(thumbColor = Color(0xff57d159), activeTrackColor = Color(0xff57d159)),
                valueRange = 0f..100f,
                steps = 10
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Tamaño de la imagen procesada en bytes: $bitmapSize")
        }

        // Imagen procesada
        bitmap2?.let {
            val aspectRatio = it.width.toFloat() / it.height.toFloat()
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Imagen procesada",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(16.dp))
                    .padding(8.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            ElevatedButton(
                onClick = { showAmpliar = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xff57d159))
            ) { Text("Ampliar Imagen") }

            Spacer(modifier = Modifier.width(16.dp))

            ElevatedButton(
                onClick = {
                    bitmap2?.let { saveCompressedImage(context, it) }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xff57d159))
            ) { Text("Guardar Imagen") }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

fun saveCompressedImage(context: Context, bitmap: Bitmap) {
    // Generar un nombre de archivo único basado en el tiempo
    val fileName = "compressed_image_${System.currentTimeMillis()}.png"

    // Crear el archivo en la memoria interna
    val file = File(context.filesDir, fileName)

    FileOutputStream(file).use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    }
}

// Función que obtiene el tamaño de la imagen en bytes
private fun getBitmapSizeInBytes(bitmap2: Bitmap): Int {
    val byteStream = ByteArrayOutputStream()
    bitmap2.compress(Bitmap
        .CompressFormat.PNG, 100, byteStream)
    return byteStream.toByteArray().size
}

// Función para comprimir la imagen utilizando QuadTree de forma asíncrona
suspend fun compressImageUsingQuadTreeAsync(bitmap: Bitmap, max: Int): Bitmap {
    return withContext(Dispatchers.Default) {  // Usamos Dispatchers.Default para mover la operación a un hilo de fondo
        compressImageUsingQuadTree(bitmap, max)  // Comprimir la imagen
    }
}

// Función para comprimir la imagen utilizando QuadTree
fun compressImageUsingQuadTree(bitmap: Bitmap, max: Int): Bitmap {

    val quadtree = Quadtree(bitmap, max = max)
    return quadtree.getCompressedBitmap()
}

data class QuadNode(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    var color: Int? = null,
    var topLeft: QuadNode? = null,
    var topRight: QuadNode? = null,
    var bottomLeft: QuadNode? = null,
    var bottomRight: QuadNode? = null
)

class Quadtree(private val bitmap: Bitmap, private val max: Int) {
    private val root: QuadNode = buildTree(0, 0, bitmap.width, bitmap.height)

    private fun buildTree(x: Int, y: Int, width: Int, height: Int): QuadNode {
        val node = QuadNode(x, y, width, height)
        if (width <= 1 || height <= 1 || isUniform(x, y, width, height)) {
            node.color = getAverageColor(x, y, width, height)
        } else {
            val halfWidth = width / 2
            val halfHeight = height / 2
            node.topLeft = buildTree(x, y, halfWidth, halfHeight)
            node.topRight = buildTree(x + halfWidth, y, halfWidth, halfHeight)
            node.bottomLeft = buildTree(x, y + halfHeight, halfWidth, halfHeight)
            node.bottomRight = buildTree(x + halfWidth, y + halfHeight, halfWidth, halfHeight)
        }
        return node
    }

    private fun isUniform(x: Int, y: Int, width: Int, height: Int): Boolean {
        val firstColor = bitmap.getPixel(x, y)
        for (i in x until x + width) {
            for (j in y until y + height) {
                if (abs(bitmap.getPixel(i, j) - firstColor) > max) {
                    return false
                }
            }
        }
        return true
    }

    private fun getAverageColor(x: Int, y: Int, width: Int, height: Int): Int {
        var r = 0
        var g = 0
        var b = 0
        val totalPixels = width * height

        for (i in x until x + width) {
            for (j in y until y + height) {
                val color = bitmap.getPixel(i, j)
                r += (color shr 16) and 0xFF
                g += (color shr 8) and 0xFF
                b += color and 0xFF
            }
        }

        r /= totalPixels
        g /= totalPixels
        b /= totalPixels

        return (0xFF shl 24) or (r shl 16) or (g shl 8) or b
    }

    fun getCompressedBitmap(): Bitmap {
        val compressedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        paintNode(root, compressedBitmap)
        return compressedBitmap
    }

    private fun paintNode(node: QuadNode, bitmap: Bitmap) {
        if (node.color != null) {
            for (i in node.x until node.x + node.width) {
                for (j in node.y until node.y + node.height) {
                    bitmap.setPixel(i, j, node.color!!)
                }
            }
        } else {
            node.topLeft?.let { paintNode(it, bitmap) }
            node.topRight?.let { paintNode(it, bitmap) }
            node.bottomLeft?.let { paintNode(it, bitmap) }
            node.bottomRight?.let { paintNode(it, bitmap) }
        }
    }
}

// funcion que me permite ampliar la imagen
@Composable
fun Ampliar(bitmap: Bitmap?, onDismiss: () -> Unit) {
    // Estado para controlar la escala y la posición
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Caja que permite la interacción con la imagen ampliada
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))  // Fondo oscuro para resaltar la imagen
            .clickable { onDismiss() }  // Al hacer clic fuera de la imagen, se cierra
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale *= zoom
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }
    ) {
        bitmap?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY
                    )
            )
        }
    }
}





