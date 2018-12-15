package com.example.dai.hukuwarai

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

    companion object {
        const val INTENT_KEY_IP = "ip_data"
        const val INTENT_KEY_PORT = "port_data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        start_button.setOnClickListener {

            val sharedPreferences = getSharedPreferences("UdpSetting", MODE_PRIVATE)
            val ip = sharedPreferences.getString("ip", "")
            val port = sharedPreferences.getString("port", "")

            if (ip!!.isBlank() && port!!.isBlank()) {
                Toast.makeText(this, "IPアドレスとボート番号を設定してください", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(INTENT_KEY_IP, ip)
                intent.putExtra(INTENT_KEY_PORT, port)
                startActivity(intent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.setting -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
