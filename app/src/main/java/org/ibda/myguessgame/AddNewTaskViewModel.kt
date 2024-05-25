package org.ibda.myguessgame

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class AddNewTaskViewModel : ViewModel() {
    private lateinit var retrofit: Retrofit
    private lateinit var taskApiService: TaskApiService

    init {
        this.retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        this.taskApiService = this.retrofit.create(TaskApiService::class.java)
        addNewTasks()
    }

    private fun addNewTasks() {
        // Nanti buat nambahin ke databasenya dari sini cy
    }
}