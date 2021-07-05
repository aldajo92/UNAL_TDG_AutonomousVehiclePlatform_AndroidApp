package com.projects.aldajo92.jetsonbotunal.main

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.InputDevice
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.niqdev.mjpeg.DisplayMode
import com.github.niqdev.mjpeg.Mjpeg
import com.github.niqdev.mjpeg.MjpegInputStream
import com.github.niqdev.mjpeg.OnFrameCapturedListener
import com.projects.aldajo92.jetsonbotunal.R
import com.projects.aldajo92.jetsonbotunal.api.SharedPreferencesManager
import com.projects.aldajo92.jetsonbotunal.api.SocketManager
import com.projects.aldajo92.jetsonbotunal.getVideoStreamingPath
import com.projects.aldajo92.jetsonbotunal.models.MoveRobotMessage
import com.projects.aldajo92.jetsonbotunal.models.RobotVelocityEncoder
import com.projects.aldajo92.jetsonbotunal.view.MultiXYWrapper
import com.projects.aldajo92.jetsonbotunal.view.SingleRealTimeWrapper
import kotlinx.android.synthetic.main.activity_main.floating_settings_button
import kotlinx.android.synthetic.main.activity_main.lineChart_control
import kotlinx.android.synthetic.main.activity_main.lineChart_input
import kotlinx.android.synthetic.main.activity_main.lineaChart_output
import kotlinx.android.synthetic.main.activity_main.progress_x
import kotlinx.android.synthetic.main.activity_main.progress_y
import kotlinx.android.synthetic.main.activity_main.videoView
import kotlinx.android.synthetic.main.activity_main.view.progressbar_x_neg
import kotlinx.android.synthetic.main.activity_main.view.progressbar_x_pos
import kotlinx.android.synthetic.main.activity_main.view.progressbar_y_neg
import kotlinx.android.synthetic.main.activity_main.view.progressbar_y_pos
import java.util.Timer
import kotlin.concurrent.fixedRateTimer
import kotlin.math.abs

class MainActivity : AppCompatActivity(), SocketManager.SocketListener, OnFrameCapturedListener {

    private val lineChartOutput by lazy {
        SingleRealTimeWrapper.getInstance(
            lineaChart_output,
            Color.rgb(200, 200, 200)
        )
    }

    private val lineChartInput by lazy {
        SingleRealTimeWrapper.getInstance(
            lineChart_input,
            Color.rgb(244, 10, 10)
        )
    }

    private val realTimeWrapper by lazy {
        MultiXYWrapper.getInstance(
            lineChart_control,
            listOf(
                ColorTemplate.getHoloBlue(),
                Color.rgb(244, 10, 10)
            )
        )
    }

    lateinit var mjpegView: Mjpeg

    private val sharedPreferencesManager by lazy {
        SharedPreferencesManager(this)
    }

    private val socketManager by lazy {
        SocketManager(
            this,
            sharedPreferencesManager.getSelectedBaseUrl() ?: ""
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        socketManager.connect()

        startVideoStream()

        floating_settings_button.setOnClickListener {
            val newFragment = ConfigurationDialog()
            newFragment.show(supportFragmentManager, "configuration")
        }

    }

    override fun onPause() {
        super.onPause()
        try {
            videoView.stopPlayback()
        } catch (e : Exception){
            Log.e("ADJ", "camera not opened")
        }
    }

    override fun onResume() {
        super.onResume()
        startVideoStream()
    }

    private fun runCommandTimer(value: Any): Timer = fixedRateTimer(
        "timer",
        true,
        0,
        200
    ) {
        sendMessage(value)
    }

    private fun sendMessage(message: Any) = socketManager.sendData(message)

    override fun onDestroy() {
        super.onDestroy()
        socketManager.disconnect()
    }

    override fun onDataReceived(robotVelocityEncoder: RobotVelocityEncoder) {
        lineChartOutput.addEntry(robotVelocityEncoder.velocityEncoder)
        lineChartInput.addEntry(robotVelocityEncoder.input)

    }

    private var globalTimer: Timer? = null
    private val minMovement = 0.1f
    private var alreadyOnZero = true

    var ly = 0f
    var rx = 0f

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        return if (event.source and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK && event.action == MotionEvent.ACTION_MOVE) {
            globalTimer?.cancel()
            val joyLy = event.getAxisValue(MotionEvent.AXIS_Y)
            val joyRx = event.getAxisValue(MotionEvent.AXIS_Z)

            ly = if (abs(joyLy) >= minMovement) -joyLy else 0f
            rx = if (abs(joyRx) >= minMovement) -joyRx else 0f

            if (ly >= 0) {
                progress_y.progressbar_y_pos.progress = (ly * 100).toInt()
                progress_y.progressbar_y_neg.progress = 0
            } else {
                progress_y.progressbar_y_pos.progress = 0
                progress_y.progressbar_y_neg.progress = (-ly * 100).toInt()
            }

            if (-rx >= 0) {
                progress_x.progressbar_x_pos.progress = (-rx * 100).toInt()
                progress_x.progressbar_x_neg.progress = 0
            } else {
                progress_x.progressbar_x_pos.progress = 0
                progress_x.progressbar_x_neg.progress = (rx * 100).toInt()
            }

            val movementMessage = MoveRobotMessage(ly, rx)
            if (abs(joyLy) >= minMovement || abs(joyRx) >= minMovement) {
                sendMessage(movementMessage)
                alreadyOnZero = false
            } else if (abs(joyLy) < minMovement && abs(joyRx) < minMovement && !alreadyOnZero) {
                sendMessage(movementMessage)
                alreadyOnZero = true
            }

            realTimeWrapper.addEntries(listOf(ly, rx))

            true
        } else super.onGenericMotionEvent(event)
    }

    private fun startVideoStream() {
        sharedPreferencesManager.getSelectedBaseUrl()?.let {
            mjpegView = Mjpeg.newInstance()
            mjpegView.open(it.getVideoStreamingPath(), 100)
                .subscribe({ inputStream: MjpegInputStream? ->
                    videoView.setSource(inputStream)
                    videoView.setDisplayMode(DisplayMode.BEST_FIT)
                    videoView.showFps(true)
                }, {
                    Toast.makeText(
                        this, "error connecting to robot",
                        Toast.LENGTH_LONG
                    ).show()
                    videoView.stopPlayback()
                })
        }

        videoView.setOnFrameCapturedListener(this)
    }

    override fun onFrameCaptured(bitmap: Bitmap?) {

    }

}