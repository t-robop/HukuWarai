package com.example.dai.hukuwarai

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import android.util.Log
import java.io.UnsupportedEncodingException


class Udp {

    private val ip = "192.168.1.9"
    private val port = 6000
    private var inetAddress: InetAddress? = null
    private var buff: ByteArray? = null
    private var socket: DatagramSocket? = null
    private var packet: DatagramPacket? = null
    private var sendTextThread: Thread? = null

    fun udpSend(sendText: String) {
        try {
            Log.d("sendText", sendText)
            buff = sendText.toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        sendTextThread = Thread(Runnable {
            try {
                inetAddress = InetAddress.getByName(ip)
                socket = DatagramSocket()
                packet = DatagramPacket(
                    buff,
                    buff!!.size,
                    inetAddress,
                    port
                )
                socket!!.send(packet)
                socket!!.close()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("da", "da")
            }
        })
        sendTextThread!!.start()
    }

}