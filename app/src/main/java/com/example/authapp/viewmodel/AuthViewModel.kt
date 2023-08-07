package com.example.authapp.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

public class AuthViewModel(application: Application) : AndroidViewModel(application) {
    var userData = MutableLiveData<FirebaseUser>()
    var loggedStatus = MutableLiveData<Boolean>()
    var registerStatus = MutableLiveData<Boolean>()
    val auth: FirebaseAuth
    var error = ""

    init {
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            userData.postValue(auth.currentUser)
        }
    }

    fun register(email: String?, pass: String?) {
        auth.createUserWithEmailAndPassword(email!!, pass!!).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                registerStatus.value = true
            } else {
                error = task.exception?.message.toString()
                registerStatus.value = false
            }
        }
    }

    fun signIn(email: String?, pass: String?) {
        auth.signInWithEmailAndPassword(email!!, pass!!).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                userData.postValue(auth.currentUser)
                loggedStatus.value = true
            } else {
                error = task.exception?.message.toString()
                loggedStatus.value = false
            }
        }
    }

    fun signOut() {
        auth.signOut()
        loggedStatus.value = false
    }
}