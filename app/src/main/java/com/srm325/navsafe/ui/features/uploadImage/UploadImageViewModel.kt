package com.srm325.navsafe.ui.features.uploadImage

import androidx.lifecycle.ViewModel
import com.srm325.navsafe.data.Repository

class UploadImageViewModel : ViewModel() {

    private val repository = Repository()

    fun getCurrentUser() = repository.getCurrentUser()

}