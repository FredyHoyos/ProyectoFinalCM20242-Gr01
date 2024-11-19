package co.edu.udea.compumovil.gr01_20242.pickerpacker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import co.edu.udea.compumovil.gr01_20242.pickerpacker.theme.PickerPackerTheme
import co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.navigation.AppNavigation


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PickerPackerTheme {
                AppNavigation()
            }
        }
    }
}



