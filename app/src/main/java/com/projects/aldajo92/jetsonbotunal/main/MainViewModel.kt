package com.projects.aldajo92.jetsonbotunal.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.niqdev.mjpeg.Mjpeg
import com.projects.aldajo92.jetsonbotunal.api.SocketManager
import com.projects.aldajo92.jetsonbotunal.models.RobotVelocityEncoder

class MainViewModel : ViewModel(), SocketManager.SocketListener {

    private val _velocityLiveData = MutableLiveData<RobotVelocityEncoder>()
    val velocityLiveData: LiveData<RobotVelocityEncoder> get() = _velocityLiveData

    private val _encoderLiveData = MutableLiveData(0f)
    val encoderLiveData: LiveData<Float> get() = _encoderLiveData

    private val mjpegView by lazy {
        Mjpeg.newInstance()
    }

    lateinit var socketManager: SocketManager

    fun connectSocket(urlPath: String) {
        if (!this::socketManager.isInitialized) {
            socketManager = SocketManager(this, urlPath)
            socketManager.connect()
        }
    }

    fun sendMessageBySocket(message: Any) = socketManager.sendData(message)

    fun disconnectSocket() {
        socketManager.disconnect()
    }

    fun getMjpegObserver(urlPath: String) = mjpegView.open(urlPath, 100)

    override fun onDataReceived(robotVelocityEncoder: RobotVelocityEncoder) {
        _velocityLiveData.postValue(robotVelocityEncoder)
//        _velocityLiveData.value = robotVelocityEncoder.velocityEncoder
//        lineChartOutput.addEntry(robotVelocityEncoder.velocityEncoder)
//        lineChartInput.addEntry(robotVelocityEncoder.input)
    }
}