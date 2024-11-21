package co.edu.udea.compumovil.gr01_20242.pickerpacker.ui.login.ui


import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser




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

    fun signOut(context: Context) {
        // Cerrar sesión en Firebase
        auth.signOut()

        // Cerrar sesión en Google con manejo de errores
        val googleSignInClient = getGoogleSignInClient(context)
        googleSignInClient.signOut()
            .addOnCompleteListener {
                Log.d("SignOut", "Google Sign-out successful")
            }
            .addOnFailureListener { e ->
                Log.e("SignOutError", "Google Sign-out failed: ${e.localizedMessage}")
            }
    }

    private fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    fun getCurrentUser() : FirebaseUser?{
        return auth.currentUser
    }

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

        viewModelScope.launch {
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

     fun startGoogleLogin(credential: AuthCredential, home: () -> Unit) {
        _isLoading.value = true
        _errorMessage.value = ""

        viewModelScope.launch {
            try {
                val authResult = auth.signInWithCredential(credential).await()
                if (authResult.user != null) {
                    Log.d("GoogleLogin", "Google login successful")
                    home()
                } else {
                    // Handle failure - Set an appropriate error message
                    _errorMessage.value = "Google login failed. Please try again."
                    Log.d("GoogleLogin", "Google login failed: User is null")
                }
            } catch (e: Exception) {
                Log.d("GoogleLoginError", "Google login failed: ${e.localizedMessage}", e)
                _errorMessage.postValue(when (e) {
                    is FirebaseAuthInvalidCredentialsException -> "Invalid credentials."
                    is FirebaseAuthUserCollisionException -> "An account already exists with this email."
                    // ... other specific exceptions ...
                    else -> "Google login failed. Please try again later."
                })
            } finally {
                _isLoading.value = false
            }
        }
    }
}

