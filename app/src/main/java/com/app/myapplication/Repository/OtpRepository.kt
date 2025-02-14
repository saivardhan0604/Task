package com.app.myapplication.Repository

import android.util.Log
import com.app.myapplication.Model.OtpCallbacks
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class OtpRepository {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun sendOtp(phoneNumber: String, activity: OtpCallbacks) {
        val formattedPhoneNumber = if (phoneNumber.startsWith("+")) phoneNumber else "+91$phoneNumber"

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(formattedPhoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity.getActivity()) // Callback binding to activity
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    activity.onVerificationCompleted(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    activity.onVerificationFailed(e)
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    activity.onCodeSent(verificationId, token)
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(verificationId: String, otpCode: String, activity: OtpCallbacks) {
        val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
        signInWithCredential(credential, activity)
    }

    fun signInWithCredential(credential: PhoneAuthCredential, activity: OtpCallbacks) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    activity.onSignInSuccess(user)
                } else {
                    activity.onSignInFailed(task.exception)
                }
            }
    }
}
