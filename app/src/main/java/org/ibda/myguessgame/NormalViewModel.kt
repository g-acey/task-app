package org.ibda.myguessgame

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class NormalViewModel : ViewModel() {
    private lateinit var retrofit: Retrofit
    private lateinit var taskApiService: TaskApiService

    val tasks = MutableLiveData<List<TaskInfo>>()
    val destination = MutableLiveData<String>("")

    val newTaskTotal = MutableLiveData<Int>(0)
    val progressTaskTotal = MutableLiveData<Int>(0)
    val doneTaskTotal = MutableLiveData<Int>(0)

    init {
        this.retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        this.taskApiService = this.retrofit.create(TaskApiService::class.java)
    }

    fun setDestination(destination: String) {
        this.destination.value = destination
        getTasksByCategoryAndStatus()
    }

    private fun getTasksByCategoryAndStatus() {
        val call = taskApiService.getTasksByCategoryAndStatus(this.destination.value.toString(),"Normal")

        call.enqueue(object : Callback<List<TaskInfo>> {
            override fun onFailure(call: Call<List<TaskInfo>>, t: Throwable) {
                Log.e("NormalViewModel", "Failed to get search results by category and status", t)
            }

            override fun onResponse(call: Call<List<TaskInfo>>, response: Response<List<TaskInfo>>) {
                if (response.isSuccessful) {
                    tasks.value = response.body()
//                    updateTaskCounts()
                } else {
                    Log.e("NormalViewModel", "Failed to get results by category and status: ${response.errorBody()?.string()}")
                }
            }
        })
    }

    fun refreshTasks() {
        getTasksByCategoryAndStatus()
    }

    fun actionText(): String {
        return when (this.destination.value) {
            "New" -> "Take"
            "In Progress" -> "Done"
            else -> "Details"
        }
    }

}
