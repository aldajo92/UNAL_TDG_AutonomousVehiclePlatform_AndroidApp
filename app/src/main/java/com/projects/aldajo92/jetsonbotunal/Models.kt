package com.projects.aldajo92.jetsonbotunal

data class RobotVelocityEncoder(
    val velocityEncoder: Float
)

data class MoveRobotMessage(
    val steering: Float,
    val throttle: Float
)
