package com.example.dai.hukuwarai

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import kotlinx.android.synthetic.main.lead_parts_dialog.view.*

class MainActivity : AppCompatActivity() {

    private var intervalTime = 0
    private var stateNum = 0
    private val maxStateNum = 3
    private val udp = Udp()
    private lateinit var ip: String
    private var port: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ip = intent.getStringExtra(StartActivity.INTENT_KEY_IP)
        port = intent.getStringExtra(StartActivity.INTENT_KEY_PORT).toInt()

        showGuideDialog(stateNum)

        udp.udpSend(ip, port, "state$stateNum")
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
                    udp.udpSend(ip, port, position)
                    intervalTime = 0
                }
            }

            MotionEvent.ACTION_UP -> {
                if (stateNum == maxStateNum) {
                    udp.udpSend(ip, port, "end")
                    stateNum = 0

                    val intent = Intent(this, ResultActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    showNextSceneDialog()
                }
            }
        }
        return true
    }

    private fun showNextSceneDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("次に進みますか？")
            setPositiveButton("OK") { _, _ ->
                stateNum++
                showGuideDialog(stateNum)
                udp.udpSend(ip, port, "state$stateNum")
            }
            setNegativeButton("もう一度", null)
            show()
        }
    }

    private fun showGuideDialog(state: Int){
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.lead_parts_dialog, null)
        builder.setView(view)
        view.next_header_text.text = "ステップ ${state+1} / ${maxStateNum+1}"

        when(state){
            0 -> {
                view.next_description_text.text = "左目の位置を決めよう！"
                view.next_parts_image.setImageResource(R.drawable.left_eye)
                title = "左目 の位置を決めています"
            }

            1 -> {
                view.next_description_text.text = "右目の位置を決めよう！"
                view.next_parts_image.setImageResource(R.drawable.right_eye)
                title = "右目 の位置を決めています"
            }

            2 -> {
                view.next_description_text.text = "鼻の位置を決めよう！"
                view.next_parts_image.setImageResource(R.drawable.nose)
                title = "鼻 の位置を決めています"
            }

            3 -> {
                view.next_description_text.text = "口の位置を決めよう！"
                view.next_parts_image.setImageResource(R.drawable.mouth)
                title = "口 の位置を決めています"
            }
        }


        builder.setCancelable(true)
        val alertDialog = builder.create()
        alertDialog.show()

        view.next_button.setOnClickListener {
            alertDialog.cancel()
        }
    }
}
