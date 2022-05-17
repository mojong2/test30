package com.example.test30

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.create_id_main.*
import kotlinx.android.synthetic.main.login_create_main.*

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        val anim_test = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.text_anim2) // 글자 애니메이션
        val anim_test1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.text_anim1) // 글자 애니메이션
        val anim_test2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.text_anim3) // 글자 애니메이션
        setContentView(R.layout.login_create_main)
        hanbatang.startAnimation(anim_test1)
        create_id_button.startAnimation(anim_test2)
        loginbutton.startAnimation(anim_test)

        //SharedPreferences에 값이 저장되어있지 않을 때
        if(MySharedPreferences.getUserId(this).isNullOrBlank() || MySharedPreferences.getUserPw(this).isNullOrBlank() || MySharedPreferences.getUserType(this).isNullOrBlank()) {

        }
        else {  //SharedPreferences에 값이 저장되어 있을 때
            Toast.makeText(this, "${MySharedPreferences.getUserId(this)}님 자동 로그인 되었습니다.", Toast.LENGTH_SHORT).show()
            if(MySharedPreferences.getUserType(this).equals("0")) {
                val intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)
                finish()
            }
            else if(MySharedPreferences.getUserType(this).equals("1")) {
                val intent = Intent(this, MainActivity3::class.java)
                startActivity(intent)
                finish()
            }
        }

        loginbutton.setOnClickListener({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_right_enter,R.anim.slide_right_exit)
        })
        create_id_button.setOnClickListener({
            val intent = Intent(this, CreateIdActivity::class.java)
            startActivity(intent)
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
            Toast.makeText(applicationContext, "한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
