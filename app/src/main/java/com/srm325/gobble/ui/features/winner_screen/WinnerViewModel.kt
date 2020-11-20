package com.srm325.gobble.ui.features.winner_screen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.srm325.gobble.data.Repository
import com.srm325.gobble.data.model.Post
import com.srm325.gobble.data.model.User
import com.google.firebase.firestore.ktx.toObject

class WinnerViewModel : ViewModel() {
    private val repository = Repository()

    val post = MutableLiveData<Post>()
    val user = MutableLiveData<User>()

    suspend fun getPost(documentId : String){
        val snapShot = repository.getPost(documentId)?.toObject<Post>()
        post.postValue(snapShot)
        getUser(snapShot?.winner ?: "")
    }

    private suspend fun getUser(userId : String){
        user.postValue(repository.getUser(userId)?.toObject<User>())
    }

    fun checkUser(email: String) = repository.checkCurrentUser(email)
}