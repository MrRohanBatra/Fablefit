package com.rohan.fablefit.ui.User

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.rohan.fablefit.ui.model.UserModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance();
    private val userRepo= UserRepository();
    private var _user = mutableStateOf<FirebaseUser?>(auth.currentUser)
    private val _userData = mutableStateOf<UserModel?>(null)
    val userData: State<UserModel?> = _userData
    val user: State<FirebaseUser?> = _user
    fun refreshUser() {
        viewModelScope.launch {
            try {
                auth.currentUser?.reload()?.await()
            } catch (e: Exception) {
                Log.e("UserViewModel","${e.message}")
            } finally {
                _user.value=null;
                _user.value = auth.currentUser
            }
        }
    }
    fun getUserData(){
        val uid=_user.value?.uid?:return
        viewModelScope.launch {
//            userRepo.updateUserData(UserModel(uid=uid))
            userRepo.getUserData(uid)
                .onSuccess {
                    _userData.value=it;
                }
                .onFailure {
                    Log.e("UserViewModel","${it.message}")
                }
        }
    }
    fun updateUserAddress(address:String){
        viewModelScope.launch {
            val uid=_user.value?.uid?:return@launch
            userRepo.UpdateUserAddress(uid,address)
                .onSuccess {

                }
                .onFailure {
                    Log.d("UserViewModel","failed to update user address")
                }
        }
    }
    fun uploadImage(context: Context, uri: Uri) {

        viewModelScope.launch {

            val uid = _user.value?.uid ?: return@launch

            userRepo.uploadImage(context, uid, uri)
                .onSuccess { response ->

                    _userData.value?.let { oldUser ->

                        val newUserData = oldUser.copy(
                            vtonImage = response.file
                        )

                        userRepo.updateUserData(newUserData)
                            .onSuccess {
                                _userData.value=null;
                                _userData.value = newUserData
                                Log.d("UserViewModel", "User updated")

                            }
                            .onFailure {
                                Log.e("UserViewModel", it.message ?: "Update failed")
                            }
                    }
                }
                .onFailure {
                    Log.e("UserViewModel", it.message ?: "Upload failed")
                }
        }
    }

    fun ensureUserExists() {
        viewModelScope.launch {

            val uid = _user.value?.uid ?: return@launch

            userRepo.getUserData(uid)
                .onSuccess {
                    _userData.value = it
                }
                .onFailure {

                    // user not found → create new user
                    val newUser = UserModel(
                        uid = uid,
                        phone = null,
                        address = emptyList(),
                        vtonImage = null,
                        type = "normal"
                    )

                    userRepo.updateUserData(newUser)
                        .onSuccess {
                            _userData.value = it.user
                        }
                }
        }
    }
}