package com.rohan.fablefit.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class AuthRepository {
    private val firebaseAuth= FirebaseAuth.getInstance();
    fun loginWithEmail(
        email: String,
        passwd: String,
        onResult: (Boolean,String?)-> Unit
    ){
        firebaseAuth.signInWithEmailAndPassword(email,passwd).addOnCompleteListener {task->
            if (task.isSuccessful){
                onResult(true,null);
            }
            else{
                onResult(false,task.exception?.message);
            }
        }
    }
    fun loginWithGoogle(
        idToken:String,
        onResult: (Boolean, String?) -> Unit
    ){
        val credential= GoogleAuthProvider.getCredential(idToken,null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task->
            if (task.isSuccessful){
                onResult(true,null);
            }
            else{
                onResult(false,task.exception?.message);
            }
        }
    }
    fun logOut(){
        firebaseAuth.signOut();
    }
}