package com.srm325.gobble.ui.features.map

import androidx.lifecycle.ViewModel
import com.srm325.gobble.data.Repository

class MapViewModel : ViewModel() {
    private val repository = Repository()
    fun getCurrentUser() = repository.getCurrentUser()

}