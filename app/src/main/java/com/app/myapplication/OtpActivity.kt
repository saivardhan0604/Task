package com.app.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.myapplication.Controller.OtpController
import com.app.myapplication.Model.OtpCallbacks
import com.app.myapplication.Repository.OtpRepository
import com.app.myapplication.databinding.ActivityGoogleBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider


class OtpActivity : AppCompatActivity(), OtpCallbacks {

    private lateinit var otpController: OtpController
    private lateinit var binding: ActivityGoogleBinding
    private  var storedVerificationId: String=""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoogleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        otpController = OtpController(OtpRepository())

        binding.btnGetOtp.setOnClickListener {
            val phoneNumber = binding.etphoneSignIn.text.toString().trim()
            if (phoneNumber.isNotEmpty() && phoneNumber.length >= 10) {
                otpController.sendOtp(phoneNumber, this)
            } else {
                Toast.makeText(this, "Enter a valid mobile number", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSignIn.setOnClickListener {
            val otpCode = binding.etotpSignIn.text.toString().trim()
            if (otpCode.isNotEmpty()) {
                if (storedVerificationId.isNotEmpty()) {
                    otpController.verifyOtp(storedVerificationId, otpCode, this)
                } else {
                    Toast.makeText(this, "Please request an OTP first.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getActivity(): Activity {
        return this
    }

    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
        otpController.signInWithCredential(credential, this)
    }

    override fun onVerificationFailed(e: FirebaseException) {
        Toast.makeText(this, "Verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
    }

    override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
        storedVerificationId = verificationId
        resendToken = token
        Toast.makeText(this, "OTP Sent", Toast.LENGTH_SHORT).show()
    }

    override fun onSignInSuccess(user: FirebaseUser?) {
        startActivity(Intent(this, MainActivity::class.java))
        Toast.makeText(this, "Welcome ${user?.phoneNumber}", Toast.LENGTH_SHORT).show()
    }

    override fun onSignInFailed(exception: Exception?) {
        Toast.makeText(this, "Verification failed ${exception?.message}", Toast.LENGTH_SHORT).show()
    }
}
