package com.app.myapplication.Controller

import com.app.myapplication.Repository.AuthRepository


class AuthController(private val authRepository: AuthRepository) {

    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        authRepository.signIn(email, password, callback)
    }

    fun register(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        authRepository.signUp(email, password, callback)
    }
    fun authenticateWithFirebase(googleIdToken: String, callback: (Boolean, String?) -> Unit) {
        authRepository.authenticateWithFirebase(googleIdToken, callback)
    }
}
