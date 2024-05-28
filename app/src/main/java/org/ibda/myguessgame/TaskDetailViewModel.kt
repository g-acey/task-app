package org.ibda.myguessgame

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import java.text.SimpleDateFormat
import java.util.Locale

class TaskDetailViewModel : ViewModel() {
    private lateinit var retrofit: Retrofit
    private lateinit var taskApiService: TaskApiService

    val updateResponse = MutableLiveData<Boolean>()

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

    val actionText = MutableLiveData<String>("")

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
                            if (task.started_time != null && task.finished_time != null) {
                                durationDetail.value = calculateDuration(task.started_time, task.finished_time)
                            }

                            actionText.value = when (task.status) {
                                "New" -> "Take"
                                "In Progress" -> "Done"
                                "Done" -> "Details"
                                else -> "Details"
                            }

                            Log.i("TaskDetailViewModel", "Task ID: ${task.task_id}, Title: ${titleDetail.value}, Description: ${task.description}, Status: ${statusDetail.value}, Category: ${task.category}, Started Time: ${task.started_time}, Finished Time: ${task.finished_time}")


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

    fun editStatus(taskId: Int) {
        val call = taskApiService.updateTaskStatus(taskId)
        call.enqueue(object : Callback<UpdateResponse> {
            override fun onResponse(call: Call<UpdateResponse>, response: Response<UpdateResponse>) {
                if (response.isSuccessful) {
                    updateResponse.value = true
                } else {
                    updateResponse.value = false
                }
            }

            override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                updateResponse.value = false
            }
        })
    }

    fun calculateDuration(startTime: String, finishTime: String): String {
        val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
        try {
            val startDate = dateFormat.parse(startTime)
            val endDate = dateFormat.parse(finishTime)

            val durationInMillis = endDate.time - startDate.time

            val days = durationInMillis / (1000 * 60 * 60 * 24)
            val hours = (durationInMillis % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
            val minutes = (durationInMillis % (1000 * 60 * 60)) / (1000 * 60)
            val seconds = ((durationInMillis % (1000 * 60 * 60)) % (1000 * 60)) / 1000

            return "$days days $hours hours $minutes minutes $seconds seconds"
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "Error calculating duration"
    }

    fun goBack() {
        this.destination.value = "Back"
    }


}