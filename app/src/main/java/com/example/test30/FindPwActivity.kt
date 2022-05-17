package com.example.test30

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.create_id_main.*
import kotlinx.android.synthetic.main.find_pw_main.*
import kotlinx.android.synthetic.main.find_pw_main.back_button
import kotlinx.android.synthetic.main.find_pw_main.text1
import kotlinx.android.synthetic.main.login_main.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


class FindPwActivity : AppCompatActivity() {
    var userId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.find_pw_main)
        check_button.isEnabled = false
        if(intent.hasExtra("name")){
            text1.setText(intent.getStringExtra("name"))
        }
        back_button.setOnClickListener({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            ActivityCompat.finishAffinity(this)
            overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
        })
        find_id_button.setOnClickListener({
            val intent = Intent(this, FindIdActivity::class.java)
            startActivity(intent)
            ActivityCompat.finishAffinity(this)
            overridePendingTransition(R.anim.slide_right_enter,R.anim.slide_right_exit)
        })
        auth_button1.setOnClickListener({
            if(text1.getText().toString().equals("") || text1.getText().toString() == null){
                error_text_1.text = "아이디를 입력해주세요."
            }
            else if(text1.getText().length < 8){
                error_text_1.text = "8자 이상 입력해주세요."
            }
            else {
                userId = text1.getText().toString()
                findPwId(userId)
            }
        })
        check_button.setOnClickListener({
            val intent = Intent(this, FindPw1Activity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
            ActivityCompat.finishAffinity(this)
            overridePendingTransition(R.anim.slide_right_enter,R.anim.slide_right_exit)
        })
    }
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        ActivityCompat.finishAffinity(this)
        overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
    }

    private fun findPwId(ID: String) {

        val retrofit = Retrofit.Builder()
            .baseUrl("http://sejongcountry.dothome.co.kr/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(FindInterface::class.java)
        val call: Call<String> = service.findPw(ID)
        call.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                if(response.isSuccessful && response.body() != null) {
                    var result = response.body().toString()
                    Log.d("Reg", "onResponse Success : " + response.toString())
                    Log.d("Reg", "onResponse Success : " + result)

                    val info = JSONObject(result)
                    val status = info.getString("status")

                    if(status.equals("true")) {
                        check_button.isEnabled = true
                        error_text_1.text = "아이디가 인증되었습니다."
                        text1.isClickable = false
                        text1.isFocusable = false
                    }
                    else if(status.equals("false")) {
                        check_button.isEnabled = false
                        error_text_1.text = "존재하지 않는 아이디입니다."
                    }
                    else {

                    }
                }
                else {
                    Log.d("Reg", "onResponse Failed")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("Reg", "error : " + t.message.toString())
            }
        })

    }

}