package com.projects.aldajo92.jetsonbotunal

data class RobotMessage(
    val value: Float,
    val message: String,
    val type: String
)

data class initialData(val userName: String, val roomName: String)
data class SendMessage(val userName: String, val messageContent: String, val roomName: String)