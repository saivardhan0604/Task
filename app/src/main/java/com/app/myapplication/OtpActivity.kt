package com.app.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.myapplication.databinding.ActivityGoogleBinding
import com.app.myapplication.databinding.ActivityMainBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {


    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var binding: ActivityGoogleBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoogleBinding.inflate(layoutInflater)

        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnGetOtp.setOnClickListener {
            val phoneNumber = binding.etphoneSignIn.text.toString().trim()
            if (phoneNumber.isNotEmpty() && phoneNumber.length >= 10) {
                sendOtp(phoneNumber)
            } else {
                Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnSignIn.setOnClickListener {
            val otpCode = binding.etotpSignIn.text.toString().trim()
            if (otpCode.isNotEmpty()) {
                verifyOtp(otpCode)
            } else {
                Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }
//        btnResendOtp.setOnClickListener {
//            val phoneNumber = etPhoneNumber.text.toString().trim()
//            if (phoneNumber.isNotEmpty()) {
//                resendOtp(phoneNumber)
//            } else {
//                Toast.makeText(this, "Enter phone number to resend OTP", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    //Send otp
    private fun sendOtp(phoneNumber: String) {
        val formattedPhoneNumber =
            if (phoneNumber.startsWith("+")) phoneNumber else "+91$phoneNumber"

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(formattedPhoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout duration
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.d("OTP", "Verification completed automatically")
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e("OTP", "Verification failed: ${e.message}")
                    Toast.makeText(
                        this@OtpActivity,
                        "Verification failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    Log.d("OTP", "OTP Code sent: $verificationId")
                    storedVerificationId = verificationId
                    resendToken = token
                    Toast.makeText(this@OtpActivity, "OTP Sent", Toast.LENGTH_SHORT).show()
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyOtp(otpCode: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, otpCode)
        signInWithPhoneAuthCredential(credential)
    }

    private fun resendOtp(phoneNumber: String) {
        val formattedPhoneNumber =
            if (phoneNumber.startsWith("+")) phoneNumber else "+91$phoneNumber"

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(formattedPhoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(
                        this@OtpActivity,
                        "Resend failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    storedVerificationId = verificationId
                    resendToken = token
                    Toast.makeText(this@OtpActivity, "OTP Resent", Toast.LENGTH_SHORT).show()
                }
            })
            .setForceResendingToken(resendToken)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    startActivity(Intent(this, MainActivity::class.java))

                    Toast.makeText(
                        this,
                        "Login successful! Welcome, ${user?.phoneNumber}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("FirebaseAuth", "Sign-in successful: ${user?.uid}")
                } else {
                    Toast.makeText(this, "OTP Verification failed", Toast.LENGTH_SHORT).show()
                    Log.e("FirebaseAuth", "Sign-in failed: ${task.exception?.message}")
                }
            }
    }

}