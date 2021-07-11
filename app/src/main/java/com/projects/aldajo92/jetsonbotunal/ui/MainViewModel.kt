package com.projects.aldajo92.jetsonbotunal.ui

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.niqdev.mjpeg.Mjpeg
import com.github.niqdev.mjpeg.MjpegInputStream
import com.github.niqdev.mjpeg.OnFrameCapturedListener
import com.projects.aldajo92.jetsonbotunal.api.SocketManager
import com.projects.aldajo92.jetsonbotunal.models.RobotVelocityEncoder
import com.projects.aldajo92.jetsonbotunal.ui.data.adapter.DataImageModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import rx.Observable
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date
import kotlin.concurrent.fixedRateTimer

class MainViewModel : ViewModel(), SocketManager.SocketListener, OnFrameCapturedListener {

    private val _velocityLiveData = MutableLiveData<RobotVelocityEncoder>()
    val velocityLiveData: LiveData<RobotVelocityEncoder> get() = _velocityLiveData

    private val _dataListLiveData = MutableLiveData<List<DataImageModel>>()
    val dataListLiveData: LiveData<List<DataImageModel>> get() = _dataListLiveData
    private val dataList = mutableListOf<DataImageModel>()

    private var bitmapFrame: Bitmap? = null

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

    fun getMJPEJObserver(urlPath: String): Observable<MjpegInputStream> =
        mjpegView.open(urlPath, 100)

    override fun onDataReceived(robotVelocityEncoder: RobotVelocityEncoder) {
        _velocityLiveData.postValue(robotVelocityEncoder)
    }

    override fun onFrameCaptured(bitmap: Bitmap) {
        this.bitmapFrame = bitmap
    }

    fun saveAllImage(path: String) {
        viewModelScope.launch {
            launch(IO) {
                dataList.forEach { dataImageModel ->
                    dataImageModel.bitmap?.let {
                        saveImage(
                            it,
                            "$path/image_${dataImageModel.timeStamp}.jpg"
                        )
                    }
                }
            }
        }
    }

    fun runCaptureTimer() = fixedRateTimer("timer", true, 0, 1000) {
        saveInstantImage()
    }

    private fun saveInstantImage() {
        val velocityValue = velocityLiveData.value?.velocityEncoder
        val directionValue = velocityLiveData.value?.direction

        dataList.add(
            DataImageModel(
                bitmapFrame,
                Date().time,
                velocityValue ?: 0f,
                directionValue ?: 0f
            )
        )

        _dataListLiveData.postValue(dataList.toList())
    }

    private fun saveImage(bitmap: Bitmap, filename: String) {
        try {
            FileOutputStream(filename).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) // bmp is your Bitmap instance
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}