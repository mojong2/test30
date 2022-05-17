package com.example.test30

import android.content.Context
import android.content.DialogInterface
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.create_id_main.*
import kotlinx.android.synthetic.main.find_id_main.*
import kotlinx.android.synthetic.main.find_id_main.back_button
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class FindIdActivity : AppCompatActivity() {
    var userId = "" //아이디 임시 저장
    var userName = "" // 이름 임시 저장
    var userPhone= "" // 본인 번호 임시 저장
    var recong = 0 // 전화번호 인증 받으면 1로 바뀜
    var authNum = "1234" // 인증 번호
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.find_id_main)
        login_button.isEnabled = false
        back_button.setOnClickListener({
            val intent = Intent(this, FindPwActivity::class.java)
            startActivity(intent)
            ActivityCompat.finishAffinity(this)
            overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
        })

        auth_button.setOnClickListener({
            userName = user_name.getText().toString()
            userPhone = user_phone1.getText().toString() + "-" + user_phone2.getText().toString() + "-" + user_phone3.getText().toString()
            checkIdPhone(userName, userPhone)
        })

        login_button.setOnClickListener({
            if(auth_num.getText().toString().equals(authNum) && recong == 1) {
                val intent = Intent(this, FindPwActivity::class.java)
                //화면 바뀌고 아이디 알려주는 코드 (아이디 저장된 변수는 userId)
                var dialog = AlertDialog.Builder(this)
                dialog.setTitle("아이디 확인 성공!")
                dialog.setMessage("아이디는 " + userId + " 입니다.")
                var dialog_listener = object: DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        when(which){
                            DialogInterface.BUTTON_POSITIVE -> {
                                intent.putExtra("name",userId)
                                startActivity(intent)
                                overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
                            }
                        }
                    }
                }
                dialog.setPositiveButton("비밀번호찾기 화면으로 가기",dialog_listener)
                dialog.show()
            }
            else {
                error_text_3.text = "인증번호가 일치하지 않습니다."
            }
        })

    }
    override fun onBackPressed() {
        val intent = Intent(this, FindPwActivity::class.java)
        startActivity(intent)
        ActivityCompat.finishAffinity(this)
        overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
    }

    private fun checkIdPhone(ID: String, PHONE: String) {

        val retrofit = Retrofit.Builder()
            .baseUrl("http://sejongcountry.dothome.co.kr/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(FindInterface::class.java)
        val call: Call<String> = service.findId(ID, PHONE)
        call.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                if(response.isSuccessful && response.body() != null) {
                    var result = response.body().toString()
                    Log.d("Reg", "onResponse Success : " + response.toString())
                    Log.d("Reg", "onResponse Success : " + result)

                    val info = JSONObject(result)
                    val status = info.getString("status")

                    if(user_name.getText().toString().equals("") || user_name.getText().toString() == null){
                        error_text_1.text ="이름을 입력해주세요."
                    }
                    else if((user_phone1.getText().toString().equals("") || user_phone1.getText().toString() == null) || (user_phone2.getText().toString().equals("") || user_phone2.getText().toString() == null) || (user_phone3.getText().toString().equals("") || user_phone3.getText().toString() == null)){
                        error_text_1.text ="전화번호를 입력해주세요."
                    }
                    else if(status.equals("true")) {
                        error_text_1.text = "인증번호가 성공적으로 보내졌습니다."
                        error_text_2.text ="전송된 인증번호를 입력해주세요."
                        userId = info.getString("ID")
                        recong = 1
                        login_button.isEnabled = true
                        user_name.isClickable = false
                        user_name.isFocusable = false
                        user_phone1.isClickable = false
                        user_phone1.isFocusable = false
                        user_phone2.isClickable = false
                        user_phone2.isFocusable = false
                        user_phone3.isClickable = false
                        user_phone3.isFocusable = false
                        //////////////////////////////////////////////////////////////
                        //확인버튼을 누를 수 있게 바꿔주는 코드
                        //////////////////////////////////////////////////////////////
                    }
                    else if(status.equals("NoPhone")) {
                        error_text_1.text ="전화번호가 존재하지 않습니다."
                    }
                    else if(status.equals("NoName")) {
                        error_text_1.text ="이름이 일치하지 않습니다."
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