package com.app.myapplication.Controller

import com.app.myapplication.Model.OtpCallbacks
import com.app.myapplication.Repository.OtpRepository
import com.google.firebase.auth.PhoneAuthCredential


class OtpController(private val otpRepository: OtpRepository) {

    fun sendOtp(phoneNumber: String, activity: OtpCallbacks) {
        otpRepository.sendOtp(phoneNumber, activity)
    }

    fun verifyOtp(verificationId: String, otpCode: String, activity: OtpCallbacks) {
        otpRepository.verifyOtp(verificationId, otpCode, activity)
    }

    fun signInWithCredential(credential: PhoneAuthCredential, activity: OtpCallbacks) {
        otpRepository.signInWithCredential(credential, activity)
    }
}
