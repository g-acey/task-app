package org.ibda.myguessgame

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.math.log

class HomeViewModel : ViewModel() {
    private lateinit var retrofit: Retrofit
    private lateinit var taskApiService: TaskApiService

    val newTaskTotal = MutableLiveData<Int>(0)
    val progressTaskTotal = MutableLiveData<Int>(0)
    val doneTaskTotal = MutableLiveData<Int>(0)

    val destination = MutableLiveData<String>("")

    init {
        this.retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        this.taskApiService = this.retrofit.create(TaskApiService::class.java)
//        getTasks()
    }

    private fun getTasks() {
        val call = taskApiService.getTasks()

        call.enqueue(object : Callback<TaskApiResponse> {
            override fun onFailure(call: Call<TaskApiResponse>, t: Throwable) {
                Log.e("HomeViewModel", "Failed to get search results", t)
            }

            override fun onResponse(
                call: Call<TaskApiResponse>,
                response: Response<TaskApiResponse>
            ) {
                if (response.isSuccessful) {
                    val results = response.body()?.task
                    if (results != null && results.isNotEmpty()) {
                        for (result in results) {
                            Log.i("HomeViewModel", "Task ID: ${result.task_id}, Title: ${result.title}, Description: ${result.description}, Status: ${result.status}, Category: ${result.category}, Started Time: ${result.started_time}, Finished Time: ${result.finished_time}")

                            when (result.status) {
                                "New" -> newTaskTotal.value = newTaskTotal.value?.plus(1)
                                "In Progress" -> progressTaskTotal.value = progressTaskTotal.value?.plus(1)
                                "Done" -> doneTaskTotal.value = doneTaskTotal.value?.plus(1)
                            }
                        }
                    } else {
                        Log.i("HomeViewModel", "No tasks found")
                    }
                } else {
                    Log.e("HomeViewModel", "Failed to get results \n ${response.errorBody()?.toString() ?: ""}")
                }
            }
        })
    }

    fun refreshTasks() {
        // Clear the current task counts
        newTaskTotal.value = 0
        progressTaskTotal.value = 0
        doneTaskTotal.value = 0

        // Call getTasks to fetch the updated task counts
        getTasks()
    }

    fun goToNav(dest: String){
        this.destination.value = dest
    }

    fun goToAddTask() {
        this.destination.value = "AddNewTask"
    }
}
