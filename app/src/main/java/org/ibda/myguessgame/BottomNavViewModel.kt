package org.ibda.myguessgame

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BottomNavViewModel : ViewModel() {
    val destination = MutableLiveData<String>("")
}