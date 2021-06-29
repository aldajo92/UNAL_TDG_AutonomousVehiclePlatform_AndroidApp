package com.projects.aldajo92.jetsonbotunal

import android.graphics.Color
import android.os.Bundle
import android.view.InputDevice
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.activity_main.lineChart_control
import kotlinx.android.synthetic.main.activity_main.lineaChart_entries
import kotlinx.android.synthetic.main.activity_main.send
import kotlinx.android.synthetic.main.activity_main.videoView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.fixedRateTimer
import kotlin.math.abs


class MainActivity : AppCompatActivity(), SocketManager.SocketListener {

    private val lineChartWrapper by lazy {
        SingleRealTimeWrapper.getInstance(
            lineaChart_entries,
            Color.rgb(200, 200, 200)
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

    private val socketManager by lazy { SocketManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        socketManager.connect()

        send.setOnClickListener {
            val movementMessage = MoveRobotMessage(0f, -0.5f)
            sendMessage(movementMessage)
        }

        startStream()

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
            launch(Dispatchers.IO) {
                delay(1000)
                timer.cancel()
            }
        }
    }

    private fun sendMessage(message: Any) = socketManager.sendData(message)

    override fun onDestroy() {
        super.onDestroy()
        socketManager.disconnect()
    }

    override fun onDataReceived(robotVelocityEncoder: RobotVelocityEncoder) {
//        Log.i("ADJGF_TAG", robotVelocityEncoder.toString())
        lineChartWrapper.addEntry(robotVelocityEncoder.velocityEncoder)
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

            val movementMessage = MoveRobotMessage(ly, rx)
            if (abs(joyLy) >= minMovement || abs(joyRx) >= minMovement) {
//                globalTimer = runCommandTimer(movementMessage)
                sendMessage(movementMessage)
                alreadyOnZero = false
            } else if (abs(joyLy) < minMovement && abs(joyRx) < minMovement && !alreadyOnZero) {
//                runStopTimer( MoveRobotMessage(0f, 0f))
                sendMessage(movementMessage)
                alreadyOnZero = true
            }

            realTimeWrapper.addEntries(listOf(ly, rx))

            true
        } else super.onGenericMotionEvent(event)
    }

    private fun startStream() {
        videoView.setUrl(VIDEO_STREAMING_PATH)
        videoView?.startStream()
    }

}

fun Float.formatTwoDecimals(): String {
    return "%.2f".format(this)
}