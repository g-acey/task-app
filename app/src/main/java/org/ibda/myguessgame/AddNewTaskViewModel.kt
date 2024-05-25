package org.ibda.myguessgame

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class AddNewTaskViewModel : ViewModel() {
    private lateinit var retrofit: Retrofit
    private lateinit var taskApiService: TaskApiService

    val addTaskResult = MutableLiveData<Boolean>()
    val destination = MutableLiveData<String>("")

    init {
        this.retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        this.taskApiService = this.retrofit.create(TaskApiService::class.java)
    }

    fun addNewTask(title: String, description: String, category: String) {
        val call = taskApiService.addTask(AddTaskRequest(title, description, category))
        call.enqueue(object : Callback<AddTaskResponse> {
            override fun onResponse(call: Call<AddTaskResponse>, response: Response<AddTaskResponse>) {
                if (response.isSuccessful) {
                    addTaskResult.value = true
                } else {
                    addTaskResult.value = false
                }
            }

            override fun onFailure(call: Call<AddTaskResponse>, t: Throwable) {
                addTaskResult.value = false
            }
        })
    }

    fun goToHome() {
        this.destination.value = "Home"
    }

}