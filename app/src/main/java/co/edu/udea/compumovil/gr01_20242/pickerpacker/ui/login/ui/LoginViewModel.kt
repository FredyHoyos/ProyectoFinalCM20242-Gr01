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

class LoginViewModel : ViewModel() {

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _loginEnable = MutableLiveData<Boolean>()
    val loginEnable: LiveData<Boolean> = _loginEnable

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun onLoginChanged(email: String, password: String) {
        _email.value = email
        _password.value = password
        _loginEnable.value = isValidateEmail(email) && isValidatePassword(password)
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

    fun onLoginSelected() {
        _isLoading.value = true
        _errorMessage.value = ""  // Limpiar el mensaje de error antes de iniciar sesión

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = auth.signInWithEmailAndPassword(email.value ?: "", password.value ?: "").await()
                _loginResult.postValue(result.user != null)
            } catch (e: Exception) {
                _loginResult.postValue(false)
                _errorMessage.postValue("Login failed: ${e.localizedMessage}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}

