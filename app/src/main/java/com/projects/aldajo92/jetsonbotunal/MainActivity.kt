package com.projects.aldajo92.jetsonbotunal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var mSocket: Socket
    private val userName: String = "hELLO"
    private val roomName: String = "ROOM"

    private val objectGSon by lazy { Gson() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        send.setOnClickListener(this)

        try {
            mSocket = IO.socket("http://192.168.0.245:6666")
            Log.d("success", mSocket.id())
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("fail", "Failed to connect")
        }

        mSocket.connect()
        mSocket.on(Socket.EVENT_CONNECT, connectedEvent)
        mSocket.on("robot-message", robotMessage)
    }

    var connectedEvent = Emitter.Listener {
        Log.i("ADJGF_TAG", "connected")
    }

    var robotMessage = Emitter.Listener {
        Log.i("ADJGF_TAG", Arrays.toString(it))
    }

    private fun sendMessage() {
        val content = "hello"
        val sendData = SendMessage(userName, content, roomName)
        val jsonData = objectGSon.toJson(sendData)
        mSocket.emit("newMessage", jsonData)
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.send -> sendMessage()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.disconnect()
    }
}