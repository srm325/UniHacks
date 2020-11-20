package com.srm325.navsafe.ui.features.menu

import androidx.lifecycle.ViewModel
import com.srm325.navsafe.data.Repository

class MenuViewModel : ViewModel() {
    private val repository = Repository()

    fun checkCurrentUser(email : String) = repository.checkCurrentUser(email)

    fun getCurrentUser() = repository.getCurrentUser()

}