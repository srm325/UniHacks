package com.srm325.navsafe.ui.features.feed

interface FeedFragmentCallback {
    fun checkCurrentUser(email : String) : Boolean
    fun getCurrentUserEmail() : String?
}