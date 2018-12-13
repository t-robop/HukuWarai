package com.example.dai.hukuwarai

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent

class MainActivity : AppCompatActivity() {

    private var intervalTime = 0
    private var stateNum = 0
    private val maxStateNum = 3
    private val udp = Udp()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("stateNum", stateNum.toString())
        udp.udpSend("state0")
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event?.x
        val y = event?.y
        val position = "$x:$y:"
        Log.d("TouchEvent", position)

        when (event?.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                intervalTime++
                if (intervalTime == 3) {
                    udp.udpSend(position)
                    intervalTime = 0
                }
            }

            MotionEvent.ACTION_UP -> {
                if (stateNum == maxStateNum) {
                    Log.d("end", "end")
                    udp.udpSend("end")
                    stateNum = 0

                    val intent = Intent(this, ResultActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    showDialog()
                }
            }
        }
        return true
    }

    private fun showDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("次に進みますか？")
            setPositiveButton("OK") { _, _ ->
                // OKをタップしたときの処理
                Log.d("stateNum", stateNum.toString())
                stateNum++
                udp.udpSend("state$stateNum")
            }
            setNegativeButton("もう一度") { _, _ ->
            }
            show()
        }
    }
}
