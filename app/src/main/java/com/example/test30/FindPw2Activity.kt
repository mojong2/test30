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
import kotlinx.android.synthetic.main.find_pw_2_main.*
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
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class FindPw2Activity : AppCompatActivity() {
    var userId = ""
    var userPw = ""
    var hashPw = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.find_pw_2_main)

        if(intent.hasExtra("userId")) {
            userId = intent.getStringExtra("userId").toString()
        }
        back_button.setOnClickListener({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            ActivityCompat.finishAffinity(this)
            overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
        })
        change_button.setOnClickListener({
            if(new_password2.getText().toString().equals(new_password1.getText().toString()) && new_password1.getText().length > 7){

                userPw = new_password1.getText().toString()
                hashPw = getHash(userPw)
                updatePw(userId, hashPw)

                val intent = Intent(this, MainActivity::class.java)
                //화면 바뀌고 아이디 알려주는 코드 (아이디 저장된 변수는 userId)
                var dialog = AlertDialog.Builder(this)
                dialog.setTitle("비밀번호 변경 성공!")
                dialog.setMessage("비밀번호를 바꾸셨습니다.")
                var dialog_listener = object: DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        when(which){
                            DialogInterface.BUTTON_POSITIVE -> {
                                startActivity(intent)
                                overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
                            }
                        }
                    }
                }
                dialog.setPositiveButton("로그인화면으로 가기",dialog_listener)
                dialog.show()
            }
            else {
                error_text_1.text = "비밀번호가 일치하지 않습니다."
            }
        })
    }
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        ActivityCompat.finishAffinity(this)
        overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
    }

    private fun updatePw(ID: String, PW: String) {

        val retrofit = Retrofit.Builder()
            .baseUrl("http://sejongcountry.dothome.co.kr/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(FindInterface::class.java)
        val call: Call<String> = service.findPw2(ID, PW)
        call.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                if(response.isSuccessful && response.body() != null) {
                    var result = response.body().toString()
                    Log.d("Reg", "onResponse Success : " + response.toString())
                    Log.d("Reg", "onResponse Success : " + result)
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

    private fun getHash(str: String) : String {
        var digest: String = ""
        digest = try {
            val sh = MessageDigest.getInstance("SHA-256")
            sh.update(str.toByteArray())
            val byteData = sh.digest()

            val hexChars = "0123456789ABCDEF"
            val hex = CharArray(byteData.size * 2)
            for(i in byteData.indices) {
                val v = byteData[i].toInt() and 0xff
                hex[i * 2] = hexChars[v shr 4]
                hex[i * 2 + 1] = hexChars[v and 0xf]
            }

            String(hex)
        } catch(e: NoSuchAlgorithmException) {
            e.printStackTrace()
            ""
        }
        return digest
    }

}