package org.ibda.myguessgame

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.POST
import retrofit2.http.PUT

interface TaskApiService {
    @GET("/api/alltask")
    fun getTasks(): Call<TaskApiResponse>

    @GET("/api/taskbycategoryandstatus")
    fun getTasksByCategoryAndStatus(
        @Query("status") status: String,
        @Query("category") category: String
    ): Call<List<TaskInfo>>

    @POST("/api/addtask")
    fun addTask(@Body addTaskRequest: AddTaskRequest): Call<AddTaskResponse>

    @PUT("/api/editstatus/{id}")
    fun updateTaskStatus(@Path("id") id: Int): Call<UpdateResponse>

}

data class UpdateResponse(
    val message: String
)