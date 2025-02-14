package com.app.myapplication.Model

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

interface OtpCallbacks {
    fun getActivity(): Activity
    fun onVerificationCompleted(credential: PhoneAuthCredential)
    fun onVerificationFailed(e: FirebaseException)
    fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken)
    fun onSignInSuccess(user: FirebaseUser?)
    fun onSignInFailed(exception: Exception?)
}