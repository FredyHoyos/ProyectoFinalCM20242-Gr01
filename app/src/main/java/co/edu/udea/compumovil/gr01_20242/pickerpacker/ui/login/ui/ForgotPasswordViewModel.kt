package co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.ui

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ForgotPasswordViewModel : ViewModel() {

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _emailEnable = MutableLiveData<Boolean>()
    val emailEnable: LiveData<Boolean> = _emailEnable

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _resetResult = MutableLiveData<Boolean?>()
    val resetResult: LiveData<Boolean?> get() = _resetResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun onEmailChanged(email: String) {
        _email.value = email
        _emailEnable.value = isValidateEmail(email)
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

    fun onResetPasswordSelected() {
        _isLoading.value = true
        _errorMessage.value = ""

        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(_email.value ?: "").await()
                _resetResult.postValue(true)
            } catch (e: Exception) {
                _resetResult.postValue(false)
                _errorMessage.postValue(e.localizedMessage ?: "An unknown error occurred")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
