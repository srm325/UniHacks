package com.srm325.navsafe.data

import com.srm325.navsafe.data.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class Repository {

    val user = Firebase.auth.currentUser
    private val db = Firebase.firestore

    fun checkCurrentUser(email : String) = email == user?.email


    fun getCurrentUser() = User(
        user?.email!!,
        user.displayName!!,
        user.photoUrl.toString()
    )

    suspend fun getUser(userId : String) : DocumentSnapshot?{
        return try {
            db.collection("users")
                .document(userId)
                .get()
                .await()
        }catch (e : Exception){
            null
        }
    }

    suspend fun getPost(documentId: String) : DocumentSnapshot?{
        return try {
            db.collection("posts")
                    .document(documentId)
                    .get()
                    .await()
        }catch (e : Exception){
            null
        }

    }
}