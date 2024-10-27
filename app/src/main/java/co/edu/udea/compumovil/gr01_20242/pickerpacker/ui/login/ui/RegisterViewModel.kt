package co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.ui

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _registerEnable = MutableLiveData<Boolean>()
    val registerEnable: LiveData<Boolean> = _registerEnable

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _registerResult = MutableLiveData<Boolean>()
    val registerResult: LiveData<Boolean> = _registerResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Actualiza los campos de email y password y habilita el botón si ambas validaciones son correctas
    fun onRegisterChanged(email: String, password: String) {
        _email.value = email
        _password.value = password
        _registerEnable.value = isValidateEmail(email) && isValidatePassword(password)
    }

    private fun isValidateEmail(email: String): Boolean {
        return if (email.isEmpty()) {
            _errorMessage.value = "Email cannot be empty"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _errorMessage.value = "Invalid email format"
            false
        } else {
            true
        }
    }

    private fun isValidatePassword(password: String): Boolean {
        return if (password.length < 6) {
            _errorMessage.value = "Password must be at least 6 characters"
            false
        } else {
            true
        }
    }

    fun onRegisterSelected() {
        _isLoading.value = true
        _errorMessage.value = ""  // Limpiar el mensaje de error antes de iniciar sesión

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email.value ?: "", password.value ?: "").await()
                _registerResult.postValue(result.user != null)
            } catch (e: Exception) {
                _errorMessage.postValue("Registration failed: ${e.localizedMessage}")
                _registerResult.postValue(false)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
