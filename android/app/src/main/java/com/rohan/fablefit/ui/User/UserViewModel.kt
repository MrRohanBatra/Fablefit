package com.rohan.fablefit.ui.User

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance();
    private val userRepo= UserRepository();
    private var _user = mutableStateOf<FirebaseUser?>(auth.currentUser)
    private var _userData
    val user: State<FirebaseUser?> = _user

    fun refreshUser() {
        viewModelScope.launch {
            try {
                auth.currentUser?.reload()?.await()
            } catch (e: Exception) {
                Log.e("UserViewModel","${e.message}")
            } finally {
                _user.value = auth.currentUser
            }
        }
    }
}