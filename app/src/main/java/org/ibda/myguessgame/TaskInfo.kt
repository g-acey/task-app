package org.ibda.myguessgame

data class TaskInfo (
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val created_time: String,
    val finished_time: String,
    val duration: String,
    val status: String
)