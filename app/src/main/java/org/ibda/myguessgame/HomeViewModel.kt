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
        getTasks()
    }

    private fun getTasks(){
        val call = taskApiService.getTasks()

        call.enqueue(object : Callback<List<TaskInfo>>{
            override fun onFailure(p0: Call<List<TaskInfo>>, p1: Throwable) {
                Log.e("MainActivity", "Failed to get search results", p1)
            }

            override fun onResponse(
                call: Call<List<TaskInfo>>,
                response: Response<List<TaskInfo>>
            ) {
                if (response.isSuccessful){
                    val results = response.body()
                    if (results!!.isNotEmpty()) {
                        for (result in results) {
                            when (result.status) {
                                "new" -> newTaskTotal.value = newTaskTotal.value?.plus(1)
                                "in progress" -> progressTaskTotal.value = progressTaskTotal.value?.plus(1)
                                "done" -> doneTaskTotal.value = doneTaskTotal.value?.plus(1)
                            }
                        }
                    }
                } else {
                    Log.e("MainActivity", "Failed to get results \n ${response.errorBody()?.toString() ?: ""}")
                }
            }
        })
    }

    fun goToNav(dest: String){
        this.destination.value = "BottomNav"
    }

    fun goToAddTask() {
        this.destination.value = "AddNewTask"
    }
}
