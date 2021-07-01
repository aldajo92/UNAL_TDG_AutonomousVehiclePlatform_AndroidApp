package com.projects.aldajo92.jetsonbotunal

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.InputDevice
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.niqdev.mjpeg.DisplayMode
import com.github.niqdev.mjpeg.Mjpeg
import com.github.niqdev.mjpeg.MjpegInputStream
import com.github.niqdev.mjpeg.OnFrameCapturedListener
import kotlinx.android.synthetic.main.activity_main.lineChart_control
import kotlinx.android.synthetic.main.activity_main.lineChart_input
import kotlinx.android.synthetic.main.activity_main.lineaChart_output
import kotlinx.android.synthetic.main.activity_main.videoView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Timer
import java.util.concurrent.atomic.AtomicReference
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
        MultiRealTimeWrapper.getInstance(
            lineChart_control,
            listOf(
                ColorTemplate.getHoloBlue(),
                Color.rgb(244, 10, 10)
            )
        )
    }

    private val mjpegView by lazy {
        Mjpeg.newInstance()
            .open(VIDEO_STREAMING_PATH, 100)
    }

    private val socketManager by lazy { SocketManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        socketManager.connect()

        startVideoStream()

        showRealtimeData()

//        val scope = CoroutineScope(Job() + Dispatchers.IO)
//        CoroutineScope(Job() + Dispatchers.IO).launch {
//            val a = runCommandTimer()
//            launch(Dispatchers.IO) {
//                delay(3000)
//                a.cancel()
//            }
//        }
    }

    private fun runCommandTimer(value: Any): Timer = fixedRateTimer(
        "timer",
        true,
        0,
        200
    ) {
        sendMessage(value)
    }

    private fun runStopTimer(value: Any) {
        CoroutineScope(Job() + Dispatchers.IO).launch {
            val timer = runCommandTimer(value)
            launch(IO) {
                delay(1000)
                timer.cancel()
            }
        }
    }

    private var counter = 0
    private fun showRealtimeData() {
        fixedRateTimer("timer", true, 0, 100) {
            CoroutineScope(IO).launch {
                counter++
                setValue(counter.toFloat())
            }
        }
    }

    private suspend fun setValue(value: Float) {
        withContext(Main) {
            lineChartOutput.addEntry(valueEncoder.get())
            lineChartInput.addEntry(valueVelocitySent.get())
        }
    }

    private fun sendMessage(message: Any) = socketManager.sendData(message)

    override fun onDestroy() {
        super.onDestroy()
        socketManager.disconnect()
    }

    var valueEncoder = AtomicReference(0f)
    var valueVelocitySent = AtomicReference(0f)
    override fun onDataReceived(robotVelocityEncoder: RobotVelocityEncoder) {
        valueEncoder.set(robotVelocityEncoder.velocityEncoder)
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

            valueVelocitySent.set(ly)

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
        mjpegView.subscribe { inputStream: MjpegInputStream? ->
            videoView.setSource(inputStream)
            videoView.setDisplayMode(DisplayMode.BEST_FIT)
            videoView.showFps(true)
        }
        videoView.setOnFrameCapturedListener(this)
    }

    override fun onFrameCaptured(bitmap: Bitmap?) {

    }

}
