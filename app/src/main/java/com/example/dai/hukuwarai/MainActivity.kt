package com.example.dai.hukuwarai

import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.lead_parts_dialog.view.*

class MainActivity : AppCompatActivity() {

    private var intervalTime = 0
    private var stateNum = 0
    private val maxStateNum = 3
    private val udp = Udp()
    private lateinit var ip: String
    private var port: Int = 0
    private lateinit var vibrator: Vibrator
    private var restartVibrateFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        ip = intent.getStringExtra(StartActivity.INTENT_KEY_IP)
        port = intent.getStringExtra(StartActivity.INTENT_KEY_PORT).toInt()

        showGuideDialog(stateNum)

        udp.udpSend(ip, port, "state$stateNum")
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val position = "$x:$y:"
        Log.d("TouchEvent", position)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                vibrator.vibrate(longArrayOf(0, 500), 0)
            }

            MotionEvent.ACTION_MOVE -> {
                helpVibrate(x, y, stateNum)
                intervalTime++
                if (intervalTime == 3) {  //リスナーのリアルタイム取得毎にUDP通信すると重すぎて落ちるので、3カウンタで間引いている
                    udp.udpSend(ip, port, position)
                    intervalTime = 0
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                vibrator.cancel()
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
        val builder = AlertDialog.Builder(this).apply {
            setMessage("次に進みますか？")
            setPositiveButton("OK") { _, _ ->
                stateNum++
                showGuideDialog(stateNum)
                udp.udpSend(ip, port, "state$stateNum")
            }
            setNegativeButton("もう一度", null)
        }

        val dialog = builder.show()

        dialog.findViewById<TextView>(android.R.id.message).apply {
            this!!.textSize = 30.0f
        }
        dialog.findViewById<Button>(android.R.id.button1).apply {
            this!!.textSize = 25.0f
            this.setPadding(30, 20, 20, 20)
        }

        dialog.findViewById<Button>(android.R.id.button2).apply {
            this!!.textSize = 25.0f
            this.setPadding(20, 20, 30, 20)
        }
    }

    private fun showGuideDialog(state: Int) {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.lead_parts_dialog, null)
        builder.setView(view)
        view.next_header_text.text = "ステップ ${state + 1} / ${maxStateNum + 1}"

        when (state) {
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

    // 動かしているパーツが正解の座標値よりも大きく外れた場合にバイブレーションする
    private fun helpVibrate(x: Float, y: Float, state: Int) {
        val margin = 100
        var currentX = 0.0
        var currentY = 0.0

        when (state) {
            0 -> {  //左目
                currentX = 247.0
                currentY = 508.0
            }
            1 -> {  //右目
                currentX = 543.0
                currentY = 506.0
            }
            2 -> {  //鼻
                currentX = 373.0
                currentY = 605.0
            }
            3 -> {  //口
                currentX = 361.0
                currentY = 854.0
            }
        }

        if ((x < currentX + margin && x > currentX - margin) && (y < currentY + margin && y > currentY - margin)) {
            vibrator.cancel()
            restartVibrateFlag = true
            //TODO 動かしているパーツの非表示
        } else {
            if (restartVibrateFlag) {
                vibrator.vibrate(longArrayOf(0, 500), 0)
                restartVibrateFlag = false
                //TODO 動かしているパーツの表示
            }
        }
    }
}
