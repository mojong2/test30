package com.example.test30

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.ERROR
import android.util.Log
import android.util.TypedValue
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.gune_write_main.*
import kotlinx.android.synthetic.main.gune_main.*
import kotlinx.android.synthetic.main.gune_write_main.*
import kotlinx.android.synthetic.main.gune_write_main.back_button
import kotlinx.android.synthetic.main.gune_write_main.contents_text
import kotlinx.android.synthetic.main.gune_write_main.size_small_button
import kotlinx.android.synthetic.main.gune_write_main.size_text
import kotlinx.android.synthetic.main.gune_write_main.size_up_button
import kotlinx.android.synthetic.main.gune_write_main.title_text
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class GuneInsertActivity : AppCompatActivity() {

    val current : Long = System.currentTimeMillis()
    val format1 = SimpleDateFormat("yyyy-MM-dd")
    val format2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var TODAY = format1.format(current)
    var userId = ""
    var boardTitle = ""
    var boardContent = ""
    var boardType = ""
    var boardStatus = ""
    var boardCrtu = ""
    var boardCrtd = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.gune_write_main)

        today_text.text = "작성일자 : " + TODAY

        //SharedPreferences에 값이 저장되어있지 않을 때
        if(MySharedPreferences.getUserId(this).isNullOrBlank() || MySharedPreferences.getUserPw(this).isNullOrBlank() || MySharedPreferences.getUserType(this).isNullOrBlank()) {

        }
        else {  //SharedPreferences에 값이 저장되어 있을 때
            userId = MySharedPreferences.getUserId(this)
        }

        back_button.setOnClickListener({
            val intent = Intent(this, SubActivity2::class.java)
            startActivity(intent)
            ActivityCompat.finishAffinity(this)
            overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
            finish()
        })
        size_up_button.setOnClickListener({
            if(size_text.text == "작게"){
                size_text.text = "중간"
                title_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,40.0f)
                contents_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,40.0f)
            }
            else if(size_text.text == "중간"){
                size_text.text = "크게"
                title_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,50.0f)
                contents_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,50.0f)
            }
        })
        size_small_button.setOnClickListener({
            if(size_text.text == "중간"){
                size_text.text = "작게"
                title_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,30.0f)
                contents_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,30.0f)
            }
            else if(size_text.text == "크게"){
                size_text.text = "중간"
                title_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,40.0f)
                contents_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,40.0f)
            }
        })
        regis_button.setOnClickListener({
            if((title_text.getText().toString().equals("") || title_text.getText().toString() == null) && (contents_text.getText().toString().equals("") || contents_text.getText().toString() == null)) {
                Toast.makeText(this, "제목과 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else if((title_text.getText().toString().equals("") || title_text.getText().toString() == null)) {
                Toast.makeText(this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else if((contents_text.getText().toString().equals("") || contents_text.getText().toString() == null)) {
                Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                boardTitle = title_text.getText().toString()
                boardContent = contents_text.getText().toString()
                if(secret_btn.isChecked == true) {
                    boardType = "1"
                }
                else {
                    boardType = "0"
                }
                boardStatus = "u"
                boardCrtu = userId
                boardCrtd = format2.format(current)
                insertGunePost(boardTitle, boardContent, boardType, boardStatus, userId, boardCrtu, boardCrtd)

                Toast.makeText(this, "게시글이 등록되었습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SubActivity2::class.java)
                startActivity(intent)
                ActivityCompat.finishAffinity(this)
                overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
            }
        })
    }
    override fun onBackPressed() {
        val intent = Intent(this, SubActivity2::class.java)
        startActivity(intent)
        ActivityCompat.finishAffinity(this)
        overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
        finish()
    }

    private fun insertGunePost(TITLE: String, CONTENT: String, TYPE: String, STATUS: String, ID: String, CRTU: String, CRTD: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://sejongcountry.dothome.co.kr/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(GuneInterface::class.java)
        val call: Call<String> = service.insertGune(TITLE, CONTENT, TYPE, STATUS, ID, CRTU, CRTD)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.isSuccessful && response.body() != null) {
                    var result = response.body().toString()
                    Log.d("Reg", "onResponse Success : " + response.toString())
                    Log.d("Reg", "onResponse Success : " + result)

                    val info = JSONObject(result)
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
