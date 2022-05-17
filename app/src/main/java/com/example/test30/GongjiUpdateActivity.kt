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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.gongji_update_main.*
import kotlinx.android.synthetic.main.gongji_update_main.back_button
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class GongjiUpdateActivity : AppCompatActivity() {

    val current : Long = System.currentTimeMillis()
    val format1 = SimpleDateFormat("yyyy-MM-dd")
    val format2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var TODAY = format1.format(current)
    var userId = ""
    var boardNo = 0
    var boardTitle = ""
    var boardContent = ""
    var boardType = ""
    var boardStatus = ""
    var boardCrtd = ""
    var boardArtu = ""
    var boardArtd = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.gongji_update_main)

        //SharedPreferences에 값이 저장되어있지 않을 때
        if(MySharedPreferences.getUserId(this).isNullOrBlank() || MySharedPreferences.getUserPw(this).isNullOrBlank() || MySharedPreferences.getUserType(this).isNullOrBlank()) {

        }
        else {  //SharedPreferences에 값이 저장되어 있을 때
            userId = MySharedPreferences.getUserId(this)
        }

        if(intent.hasExtra("NO")){
            boardNo = intent.getIntExtra("NO", 0)
        }
        if(intent.hasExtra("TITLE")){
            boardTitle = intent.getStringExtra("TITLE").toString()
            title_text.setText(boardTitle)
        }
        if(intent.hasExtra("CONTENT")){
            boardContent = intent.getStringExtra("CONTENT").toString()
            contents_text.setText(boardContent)
        }
        if(intent.hasExtra("CRTD")){
            boardCrtd = intent.getStringExtra("CRTD").toString()
            val WDAY1 = LocalDateTime.parse(boardCrtd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            val WDAY2 = WDAY1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            today_text.text = "작성일자 : " + WDAY2
        }

        back_button.setOnClickListener({
            val intent = Intent(this, GongjiSubActivity::class.java)
            intent.putExtra("NO", boardNo)
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
            if((title_text.getText().toString().equals("") || title_text.getText().toString() == null) && (contents_text.getText().toString().equals("") || contents_text.getText().toString() == null)){
                Toast.makeText(this, "제목과 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else if(title_text.getText().toString().equals("") || title_text.getText().toString() == null){
                Toast.makeText(this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else if(contents_text.getText().toString().equals("") || contents_text.getText().toString() == null){
                Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                boardTitle = title_text.getText().toString()
                boardContent = contents_text.getText().toString()
                if(import_btn.isChecked == true) {  //중요 공지
                    boardType = "1"
                }
                else {  //일반 공지
                    boardType = "0"
                }
                boardArtu = userId
                boardArtd = format2.format(current)
                updateBoardPost(boardNo, boardTitle, boardContent, boardType, boardArtu, boardArtd)

                Toast.makeText(this, "게시글이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SubActivity::class.java)
                startActivity(intent)
                ActivityCompat.finishAffinity(this)
                overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
            }
        })
    }
    override fun onBackPressed() {
        val intent = Intent(this, GongjiSubActivity::class.java)
        intent.putExtra("NO", boardNo)
        startActivity(intent)
        ActivityCompat.finishAffinity(this)
        overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
        finish()
    }

    private fun updateBoardPost(NO: Int, TITLE: String, CONTENT: String, TYPE: String, ARTU: String, ARTD: String) {

        val retrofit = Retrofit.Builder()
            .baseUrl("http://sejongcountry.dothome.co.kr/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(BoardInterface::class.java)
        val call: Call<String> = service.updateBoard(NO, TITLE, CONTENT, TYPE, ARTU, ARTD)
        call.enqueue(object : Callback<String> {
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

}