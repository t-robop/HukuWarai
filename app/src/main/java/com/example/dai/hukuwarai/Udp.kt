package com.example.dai.hukuwarai

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import android.util.Log
import java.io.UnsupportedEncodingException


class Udp {

    //private val ip = "192.168.1.17"
    //private val port = 6000
    private lateinit var iNetAddress : InetAddress
    private lateinit var buff: ByteArray
    private lateinit var socket: DatagramSocket
    private lateinit var packet: DatagramPacket
    private lateinit var sendTextThread: Thread

    fun udpSend(ip: String, port: Int, sendText: String) {
        try {
            buff = sendText.toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            Log.d("UdpError", e.toString())
        }

        sendTextThread = Thread(Runnable {
            try {
                iNetAddress = InetAddress.getByName(ip)
                socket = DatagramSocket()
                packet = DatagramPacket(
                    buff,
                    buff.size,
                    iNetAddress,
                    port
                )
                socket.send(packet)
                socket.close()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("UdpError", e.toString())
            }
        })
        sendTextThread.start()
    }

}