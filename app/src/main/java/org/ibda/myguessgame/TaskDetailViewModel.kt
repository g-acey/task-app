package org.ibda.myguessgame

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class TaskDetailViewModel : ViewModel() {
    private lateinit var retrofit: Retrofit
    private lateinit var taskApiService: TaskApiService

    val taskResult = MutableLiveData<TaskInfo?>()
    val destination = MutableLiveData<String>("")
    val error = MutableLiveData<String>()

    val titleDetail = MutableLiveData<String>("")
    val detailDetail = MutableLiveData<String>("")
    val categoryDetail = MutableLiveData<String>("")
    val statusDetail = MutableLiveData<String>("")
    val createdTimeDetail = MutableLiveData<String>("")
    val finishTimeDetail = MutableLiveData<String>("")
    val durationDetail = MutableLiveData<String>("")

    init {
        this.retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        this.taskApiService = this.retrofit.create(TaskApiService::class.java)
    }

    fun taskDetail(taskId: Int) {
        val call = taskApiService.getTasks()
        call.enqueue(object : Callback<TaskApiResponse> {
            override fun onResponse(call: Call<TaskApiResponse>, response: Response<TaskApiResponse>) {
                if (response.isSuccessful) {
                    val taskApiResponse = response.body()
                    if (taskApiResponse != null) {
                        val task = taskApiResponse.task.find { it.task_id == taskId }
                        if (task != null) {
                            // Update LiveData for each detail
                            titleDetail.value = task.title
                            detailDetail.value = task.description
                            categoryDetail.value = task.category
                            statusDetail.value = task.status
                            createdTimeDetail.value = task.started_time.toString()
                            finishTimeDetail.value = task.finished_time?.toString() ?: "Not finished yet"
                            // Calculate and update duration if needed
                            // durationDetail.value = calculateDuration(task.startedTime, task.finishedTime)
                        } else {
                            error.value = "Task with ID $taskId not found"
                            Log.e("TaskDetailViewModel", "Task with ID $taskId not found")
                        }
                    } else {
                        error.value = "Empty response"
                        Log.e("TaskDetailViewModel", "Empty response")
                    }
                } else {
                    error.value = "Failed to fetch tasks: ${response.message()}"
                    Log.e("TaskDetailViewModel", "Failed to fetch tasks: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<TaskApiResponse>, t: Throwable) {
                error.value = "Failed to fetch tasks: ${t.message}"
                Log.e("TaskDetailViewModel", "Failed to fetch tasks: ${t.message}")
            }
        })
    }

}