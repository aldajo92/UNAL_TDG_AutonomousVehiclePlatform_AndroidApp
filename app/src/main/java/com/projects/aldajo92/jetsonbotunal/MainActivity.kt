package com.projects.aldajo92.jetsonbotunal

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.lineaChart_entries
import kotlinx.android.synthetic.main.activity_main.send


class MainActivity : AppCompatActivity(), SocketManager.SocketListener {

    private val userName: String = "hELLO"
    private val roomName: String = "ROOM"

    private val lineChartWrapper by lazy {
        LineChartWrapper.getInstance(lineaChart_entries)
    }

    private val socketManager by lazy {
        SocketManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        socketManager.connect()

        send.setOnClickListener{ sendMessage() }
    }

    private fun sendMessage() {
//        val sendData = SendMessage(userName, "hello", roomName)
//        socketManager.sendData(sendData)
    }

    override fun onDestroy() {
        super.onDestroy()
        socketManager.disconnect()
    }

    override fun onDataReceived(robotVelocityEncoder: RobotVelocityEncoder) {
        Log.i("ADJGF_TAG", robotVelocityEncoder.toString())
        lineChartWrapper.addValue(robotVelocityEncoder.velocityEncoder)
    }

}