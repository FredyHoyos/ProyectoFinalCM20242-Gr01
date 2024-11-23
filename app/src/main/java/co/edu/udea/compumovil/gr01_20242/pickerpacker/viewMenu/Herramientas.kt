package co.edu.udea.compumovil.gr01_20242.pickerpacker.viewMenu

import android.Manifest
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
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.launch
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
    var showSnackbar by remember { mutableStateOf(false) }

    // Comprobamos si se aceptan los permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.CAMERA] == true && permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true) {
                // Permisos aceptados
                Toast.makeText(context, "Permisos otorgados", Toast.LENGTH_SHORT).show()
            } else {
                // Permisos denegados
                //Toast.makeText(context, "Permisos denegados", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

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
            .fillMaxSize()
            .background(Color(0xFFF0F4F8)),
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

        // Mostrar el slider y tamaño de la imagen
        bitmap?.let {
            Text(
                text = "Tamaño de la imagen inicial: ${getBitmapSizeInBytes(it)} bytes",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        // Slider para la compresión
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
                onValueChange = { sliderValue = it },
                onValueChangeFinished = {
                    threshold = sliderValue.toInt()
                    scope.launch {
                        bitmap2 = compressImageUsingQuadTree(bitmap!!, threshold)
                        bitmapSize = getBitmapSizeInBytes(bitmap2!!)
                    }
                },
                colors = SliderDefaults.colors(thumbColor = Color(0xff57d159), activeTrackColor = Color(0xff57d159)),
                valueRange = 0f..10f,
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
                    bitmap2?.let {
                        saveCompressedImage(context, it)
                        showSnackbar = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xff57d159))
            ) { Text("Guardar Imagen") }
        }

        // Mostrar Snackbar de imagen guardada
        if (showSnackbar) {
            Snackbar(
                action = {
                    Button(onClick = { showSnackbar = false },
                           colors = ButtonDefaults.buttonColors(containerColor = Color(0xff57d159)),
                    ) {
                        Text("Cerrar")
                    }
                },
                modifier = Modifier.padding(16.dp),
                containerColor = Color(0xFF8de88a)
            ) {
                Text("Imagen Guardada")
            }
        }
    }
}

// Función para guardar la imagen comprimida
fun saveCompressedImage(context: Context, bitmap: Bitmap) {
    val fileName = "compressed_image_${System.currentTimeMillis()}.png"
    val file = File(context.filesDir, fileName)
    FileOutputStream(file).use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    }
}

// Función para obtener el tamaño en bytes
fun getBitmapSizeInBytes(bitmap2: Bitmap): Int {
    val byteStream = ByteArrayOutputStream()
    bitmap2.compress(Bitmap.CompressFormat.PNG, 100, byteStream)
    return byteStream.toByteArray().size
}


// Función para comprimir la imagen utilizando QuadTree
suspend fun compressImageUsingQuadTree(bitmap: Bitmap, max: Int): Bitmap {
    // creo un nodo raiz
    val rootNode = QuadtreeNode()
//creo un quadtree
    val quadtree = Quadtree1()
// contruyo el quadtree con la imagen y el maximo valor de profundidad del árbol
    val arbol = quadtree.buildQuadtree(bitmap, max)
// Reconstruyo la imagen from the quadtree
    val reconstructedImage = quadtree.reconstructImage(arbol, bitmap.width, bitmap.height)

    return withContext(Dispatchers.Default) {  // Usamos Dispatchers.Default para mover la operación a un hilo de fondo

        reconstructedImage
    }

}

// lógica de compresión de imagen usando un quadtree
//*********************************************************************************************

// clase del nodo raiz del quatree
data class QuadtreeNode(
    var color: Int = Color.Transparent.toArgb(), // Utiliza `Color.Transparent.toArgb()` para convertir a entero
    var isLeaf: Boolean = true,
    var children: Array<QuadtreeNode?>? = null
)
// clase del quadtree
class Quadtree1 {
    private var root: QuadtreeNode? = null

    init {
        root = QuadtreeNode()
    }
    // Método para construir el QuadTree
    fun buildQuadtree(image: Bitmap, maxDepth: Int): QuadtreeNode? {
        root = buildQuadtreeRecursive(image, maxDepth, 0, 0, image.width, image.height)
        return root
    }
    // metodo recursivo para construir el quadtree
    private fun buildQuadtreeRecursive(image: Bitmap, maxDepth: Int, x: Int, y: Int, width: Int, height: Int): QuadtreeNode? {
        return if (maxDepth == 0 || isHomogeneous(image, x, y, width, height)) {
            val leafNode = QuadtreeNode()
            leafNode.color = calculateAverageColor(image, x, y, width, height)
            leafNode.isLeaf = true
            leafNode
        } else {
            val parentNode = QuadtreeNode()
            parentNode.isLeaf = false
            parentNode.children = arrayOfNulls(4)

            val subWidth = width / 2
            val subHeight = height / 2

            parentNode.children!![0] = buildQuadtreeRecursive(image, maxDepth - 1, x, y, subWidth, subHeight) // Top left
            parentNode.children!![1] = buildQuadtreeRecursive(image, maxDepth - 1, x + subWidth, y, subWidth, subHeight) // Top right
            parentNode.children!![2] = buildQuadtreeRecursive(image, maxDepth - 1, x, y + subHeight, subWidth, subHeight) // Bottom left
            parentNode.children!![3] = buildQuadtreeRecursive(image, maxDepth - 1, x + subWidth, y + subHeight, subWidth, subHeight) // Bottom right

            return parentNode
        }
    }
    // metodo para verificar si el nodo es homogeneo
    private fun isHomogeneous(image: Bitmap, x: Int, y: Int, width: Int, height: Int): Boolean {
        val firstColor = image.getPixel(x, y)
        for (i in x until x + width) {
            for (j in y until y + height) {
                if (image.getPixel(i, j) != firstColor) {
                    return false
                }
            }
        }
        return true
    }
    // Función para calcular el color promedio
    private fun calculateAverageColor(image: Bitmap, x: Int, y: Int, width: Int, height: Int): Int {
        var totalRed = 0
        var totalGreen = 0
        var totalBlue = 0
        var pixelCount = 0

        for (i in x until x + width) {
            for (j in y until y + height) {
                val pixelColor = image.getPixel(i, j)
                totalRed += (pixelColor shr 16) and 0xFF
                totalGreen += (pixelColor shr 8) and 0xFF
                totalBlue += pixelColor and 0xFF
                pixelCount++
            }
        }

        val avgRed = totalRed / pixelCount
        val avgGreen = totalGreen / pixelCount
        val avgBlue = totalBlue / pixelCount

        // Utiliza Color.rgb para crear un color a partir de RGB
        return Color(avgRed, avgGreen, avgBlue).toArgb()
    }
    // metodo para reconstruir la imagen
    fun reconstructImage(rootNode: QuadtreeNode?, width: Int, height: Int): Bitmap {
        val reconstructedImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        reconstructImageRecursive(rootNode, reconstructedImage, 0, 0, width, height)
        return reconstructedImage
    }
    // metodo recursivo para reconstruir la imagen
    private fun reconstructImageRecursive(node: QuadtreeNode?, image: Bitmap, x: Int, y: Int, width: Int, height: Int) {
        if (node == null) return
        if (node.isLeaf) {
            for (i in x until x + width) {
                for (j in y until y + height) {
                    image.setPixel(i, j, node.color)
                }
            }
        } else {
            val subWidth = width / 2
            val subHeight = height / 2

            reconstructImageRecursive(node.children!![0], image, x, y, subWidth, subHeight) // Top left
            reconstructImageRecursive(node.children!![1], image, x + subWidth, y, subWidth, subHeight) // Top right
            reconstructImageRecursive(node.children!![2], image, x, y + subHeight, subWidth, subHeight) // Bottom left
            reconstructImageRecursive(node.children!![3], image, x + subWidth, y + subHeight, subWidth, subHeight) // Bottom right
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





