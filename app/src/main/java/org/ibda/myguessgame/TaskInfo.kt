package org.ibda.myguessgame

data class TaskInfo(
    val task_id: Int,
    val title: String,
    val description: String,
    val status: String,
    val category: String,
    val started_time: String,
    val finished_time: String?
)