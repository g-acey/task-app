package org.ibda.myguessgame

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val destination = MutableLiveData<String>("")

    fun setDestination(destination: String) {
        this.destination.value = destination
        Log.d("M", this.destination.value.toString())
    }
}