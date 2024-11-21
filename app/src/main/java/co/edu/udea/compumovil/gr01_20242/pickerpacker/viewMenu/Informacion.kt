package co.edu.udea.compumovil.gr01_20242.pickerpacker.viewMenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Informacion() {

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F8))
            .padding(16.dp) // Añadir algo de margen
            .verticalScroll(scrollState)

    ) {
        Text(
            text = "Bienvenido a PickerPacker",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(bottom = 16.dp) // Espacio entre el título y el texto
        )

        Text(
            text = "¿Qué es PickerPacker?",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "PickerPacker es una aplicación de compresión de imágenes que utiliza un algoritmo basado en árboles cuartiles para reducir el tamaño de las imágenes, manteniendo la calidad de forma eficiente. El objetivo es optimizar el espacio sin perder demasiada resolución visual.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp)) // Espacio entre secciones

        Text(
            text = "Opciones del menú de navegación",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Descripción de las opciones del menú
        Text(
            text = "1. **Información**: Esta pantalla muestra los detalles sobre el uso y la funcionalidad de la aplicación.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "2. **Herramientas**: En esta sección podrás tomar una foto usando la cámara, subir una imagen desde la galería, o restablecer la imagen a un valor predeterminado. Además, ofrece una funcionalidad para procesar la imagen cargada mediante un algoritmo de compresión basado en QuadTree, lo que reduce su tamaño manteniendo los detalles más importantes. El usuario también puede ver el tamaño de la imagen original y procesada, y tiene la opción de guardar la imagen comprimida.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "3. **Galería**: Aquí podrás ver todas las imágenes comprimidas y guardadas que has procesado. Se mostrará un listado de todas las imágenes que has subido y comprimido.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "4. **Cerrar sesión**: Esta opción te permitirá salir de tu cuenta y finalizar la sesión actual.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )


        // Información del equipo
        Text(
            text = "Este proyecto fue realizado por:",
            style = MaterialTheme.typography.titleMedium,  // Estilo para títulos más pequeños
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Fredy Hoyos, Daniel Ramírez y Dorian García, estudiantes de la Universidad de Antioquia.",
            style = MaterialTheme.typography.bodyMedium,  // Estilo para el cuerpo del texto
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Información del curso
        Text(
            text = "Materia: Computación Móvil - Proyecto Final",
            style = MaterialTheme.typography.bodyMedium,  // Estilo para cuerpo de texto
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Semestre: 2024-2",
            style = MaterialTheme.typography.bodyMedium,  // Estilo para cuerpo de texto
            modifier = Modifier.padding(bottom = 16.dp)
        )


        Spacer(modifier = Modifier.height(16.dp)) // Espacio adicional al final

        // Puedes agregar un pequeño pie de página si lo deseas
        Text(
            text = "© 2024 PickerPacker.",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}