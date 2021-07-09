package com.projects.aldajo92.jetsonbotunal.ui

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.niqdev.mjpeg.Mjpeg
import com.github.niqdev.mjpeg.MjpegInputStream
import com.github.niqdev.mjpeg.OnFrameCapturedListener
import com.projects.aldajo92.jetsonbotunal.api.SocketManager
import com.projects.aldajo92.jetsonbotunal.models.RobotVelocityEncoder
import rx.Observable

class MainViewModel : ViewModel(), SocketManager.SocketListener, OnFrameCapturedListener {

    private val _velocityLiveData = MutableLiveData<RobotVelocityEncoder>()
    val velocityLiveData: LiveData<RobotVelocityEncoder> get() = _velocityLiveData

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

    fun getMJPEJObserver(urlPath: String): Observable<MjpegInputStream> = mjpegView.open(urlPath, 100)

    override fun onDataReceived(robotVelocityEncoder: RobotVelocityEncoder) {
        _velocityLiveData.postValue(robotVelocityEncoder)
    }

    override fun onFrameCaptured(bitmap: Bitmap?) {

    }
}