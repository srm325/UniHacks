package com.srm325.gobble.ui.features.menu

import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.google.android.material.button.MaterialButton
import com.srm325.gobble.R
import com.srm325.gobble.data.Repository

class MenuViewModel : ViewModel() {
    private val repository = Repository()

    fun checkCurrentUser(email : String) = repository.checkCurrentUser(email)

    fun getCurrentUser() = repository.getCurrentUser()

}