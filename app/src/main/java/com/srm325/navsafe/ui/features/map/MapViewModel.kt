package com.srm325.navsafe.ui.features.map

import androidx.lifecycle.ViewModel
import com.srm325.navsafe.data.Repository

class MapViewModel : ViewModel() {
    private val repository = Repository()
    fun getCurrentUser() = repository.getCurrentUser()

}