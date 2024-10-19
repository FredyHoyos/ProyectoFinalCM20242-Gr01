package co.edu.udea.compumovil.gr01_20242.pickerpacker.viewMenu


import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Herramientas() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Button(onClick = { }) {
            Text(text = "Tomar foto")
        }
        Button(onClick = { /* Acción para Subir foto */ }) {
            Text(text = "Subir foto")
        }
        Button(onClick = { /* Acción para Configurar */ }) {
            Text(text = "Configurar")
        }
    }
}

@Composable
private fun foto() {
    val context = LocalContext.current
    var hasCameraPermission by remember { mutableStateOf(false) }
    var isCameraInitialized by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) isCameraInitialized = true
    }

    LaunchedEffect(Unit) {
        hasCameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (!hasCameraPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
        else isCameraInitialized = true
    }

    if (hasCameraPermission) {
        if (isCameraInitialized) {
            Box(modifier = Modifier.fillMaxSize()) {
                vistaCamara()

                Button(
                    onClick = { takePicture(context) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(110.dp)
                ) {
                    Text("Tomar Foto")
                }
            }
        }
    } else {
        Text("Esperando permiso de cámara...")
    }
}

@Composable
fun vistaCamara() {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(ctx as LifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview)
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

private fun showSaveDialog(context: Context) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle("Guardar foto")
    builder.setMessage("¿Quieres guardar la foto?")
    builder.setPositiveButton("Sí") { _, _ ->
        takePicture(context)  // Llama a la función que toma la foto
    }
    builder.setNegativeButton("No") { dialog, _ ->
        dialog.dismiss()  // Cierra el diálogo sin hacer nada
    }
    val dialog = builder.create()
    dialog.show()
}

private fun takePicture(context: Context) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener(Runnable {
        val cameraProvider = cameraProviderFuture.get()
        val imageCapture = ImageCapture.Builder().build()
        val photoFile = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(context as LifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, imageCapture)

            imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        // Notificar al usuario o actualizar la UI
                    }
                    override fun onError(exception: ImageCaptureException) {
                        // Manejo de errores
                    }
                }
            )
        } catch (exc: Exception) {
            // Manejo de excepciones
        }
    }, ContextCompat.getMainExecutor(context))
}
