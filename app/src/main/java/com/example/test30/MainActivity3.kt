package com.example.test30

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.real_main.*
import kotlinx.android.synthetic.main.real_main.ge_button
import kotlinx.android.synthetic.main.real_main.gong_button
import kotlinx.android.synthetic.main.real_main.gun_button
import kotlinx.android.synthetic.main.real_main.lt_button
import kotlinx.android.synthetic.main.real_main.setting_button
import kotlinx.android.synthetic.main.real_main.singo_button
import kotlinx.android.synthetic.main.real_main_admin.*

class MainActivity3 : AppCompatActivity() {
    var mBackWait : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.real_main_admin)

        singo_button.setOnLongClickListener({
            Toast.makeText(applicationContext,"구급차를 요청했습니다",Toast.LENGTH_SHORT).show()
            true
        })

        ge_button.setOnClickListener({
            ge_button.visibility = View.GONE
            gun_button.visibility = View.VISIBLE
            gong_button.visibility = View.VISIBLE
        })
        lt_button.setOnClickListener({
            lt_button.visibility = View.GONE
            announce_speak_button.visibility = View.VISIBLE
            announce_write_button.visibility = View.VISIBLE
        })
        announce_write_button.setOnClickListener({
            val intent = Intent(this, GongjiInsertActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_right_enter,R.anim.slide_right_exit)
        })
        announce_speak_button.setOnClickListener({
            val intent = Intent(this, AnnounceSpeakActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_right_enter,R.anim.slide_right_exit)
        })
        gong_button.setOnClickListener({
            ge_button.visibility = View.VISIBLE
            gun_button.visibility = View.GONE
            gong_button.visibility = View.GONE
            val intent = Intent(this, SubActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_right_enter,R.anim.slide_right_exit)
        })
        gun_button.setOnClickListener({
            ge_button.visibility = View.VISIBLE
            gun_button.visibility = View.GONE
            gong_button.visibility = View.GONE
            val intent = Intent(this, SubActivity2::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_right_enter,R.anim.slide_right_exit)
        })
        setting_button.setOnClickListener({
            ge_button.visibility = View.VISIBLE
            gun_button.visibility = View.GONE
            gong_button.visibility = View.GONE
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_right_enter,R.anim.slide_right_exit)
        })
        Chat_button1.setOnClickListener({
            val intent = Intent(this, ChatBotActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_right_enter,R.anim.slide_right_exit)
        })
        user_menu1.setOnClickListener({
            val intent = Intent(this, UserMenuActivity::class.java)
            startActivity(intent)
            ActivityCompat.finishAffinity(this)
            overridePendingTransition(R.anim.slide_right_enter,R.anim.slide_right_exit)
        })
    }

    var time3: Long = 0
    override fun onBackPressed() {
        val time1 = System.currentTimeMillis()
        val time2 = time1 - time3
        if (time2 in 0..2000) {
            ActivityCompat.finishAffinity(this)
            System.exit(0)
        }
        else {
            time3 = time1
            Toast.makeText(applicationContext, "한번 더 누르시면 종료됩니다.",Toast.LENGTH_SHORT).show()
        }
    }
}
