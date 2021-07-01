package com.projects.aldajo92.jetsonbotunal

const val ROBOT_MESSAGE = "robot-message"
const val ROBOT_COMMAND = "robot-command"

const val BASE_URL = "http://192.168.0.123"
const val DATA_SOCKET_PATH = "$BASE_URL:5170"
const val VIDEO_STREAMING_PATH = "$BASE_URL:8080/stream?topic=/camera_processing/camera/image_color/BGR/raw"


//const val BASE_URL = "https://jetsonbotunal.ngrok.io"
//const val DATA_SOCKET_PATH = "$BASE_URL"
//const val VIDEO_STREAMING_PATH = "$BASE_URL/stream?topic=/camera_processing/camera/image_color/BGR/raw"
//const val BASE_URL = "http://192.168.0.245:5170"
