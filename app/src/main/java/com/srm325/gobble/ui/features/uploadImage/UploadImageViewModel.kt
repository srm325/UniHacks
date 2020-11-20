package com.srm325.gobble.ui.features.uploadImage

import androidx.lifecycle.ViewModel
import com.srm325.gobble.data.Repository

class UploadImageViewModel : ViewModel() {

    private val repository = Repository()

    fun getCurrentUser() = repository.getCurrentUser()

}