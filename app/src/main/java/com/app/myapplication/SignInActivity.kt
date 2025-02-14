package com.app.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.app.myapplication.Controller.AuthController
import com.app.myapplication.Repository.AuthRepository
import com.app.myapplication.databinding.ActivitySignInBinding
import com.app.myapplication.databinding.ActivitySplashScreenBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<IntentSenderRequest>
private  lateinit var binding:ActivitySignInBinding
    private lateinit var authController: AuthController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)

        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        authController = AuthController(AuthRepository())



        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("AIzaSyB0oLVrNfmrlKNNjqRfXAFhbQwyggWm_GA") // Replace with Firebase OAuth Web Client ID
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { intent ->
                    try {
                        val credential = Identity.getSignInClient(this)
                            .getSignInCredentialFromIntent(intent)

                        val googleIdToken = credential.googleIdToken
                        if (!googleIdToken.isNullOrEmpty()) {
                            authController.authenticateWithFirebase(googleIdToken) { success, message ->
                                if (success) {
                                    startActivity(Intent(this, MainActivity::class.java))
                                    Toast.makeText(this, "Welcome $message", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Sign-in failed: $message", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this, "Sign-in failed: No ID token", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e("GoogleSignIn", "Sign-in failed: ${e.message}")
                        Toast.makeText(this, "Sign-in error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Sign-in canceled", Toast.LENGTH_SHORT).show()
            }
        }


        binding.btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }

       binding.btnphoneSignIn.setOnClickListener {
            startActivity(Intent(this, OtpActivity::class.java))
        }

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmailSignIn.text.toString()
            val password = binding.etPasswordSignIn.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                authController.login(email, password) { success, error ->
                    if (success) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        Toast.makeText(this, "Failed: $error", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Fields should not be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvGoToSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun signInWithGoogle() {
        val signInClient = Identity.getSignInClient(this)

        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()

        signInClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent).build()
                googleSignInLauncher.launch(intentSenderRequest)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Sign-In error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }






}