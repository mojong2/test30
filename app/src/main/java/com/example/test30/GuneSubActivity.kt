package com.example.test30

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.opinion_list_item.view.*
import kotlinx.android.synthetic.main.gune_sub_main.*
import kotlinx.android.synthetic.main.gune_sub_main.size_small_button
import kotlinx.android.synthetic.main.gune_sub_main.size_text
import kotlinx.android.synthetic.main.gune_sub_main.size_up_button
import kotlinx.android.synthetic.main.gune_sub_main.Sound_button
import kotlinx.android.synthetic.main.gune_main.*
import kotlinx.android.synthetic.main.gune_main.back_button
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class GuneSubActivity : AppCompatActivity() {
    var boardNo = 0
    var boardTitle = ""
    var boardContent = ""
    var boardCrtd = ""
    var userId = ""
    private var tts: TextToSpeech? = null
    val CONTEXT : Context = this
    private fun initTextToSpeech(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            Toast.makeText(this,"SDK version is low", Toast.LENGTH_SHORT).show()
            return
        }
        tts = TextToSpeech(this){
            if(it == TextToSpeech.SUCCESS){
                val result = tts?.setLanguage(Locale.KOREAN)
                if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                    Toast.makeText(this,"Language not supported", Toast.LENGTH_SHORT).show()
                }
                Toast.makeText(this,"TTS setting successed", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"TTS init failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun ttsSpeak(strTTS: String){
        tts?.speak(strTTS,TextToSpeech.QUEUE_ADD,null,null)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gune_sub_main)
        contents1_text.movementMethod = ScrollingMovementMethod.getInstance()
        contents1_text.post {
            val scrollAmount = contents1_text.layout.getLineTop(contents1_text.lineCount) - contents1_text.height
            if (scrollAmount > 0)
                contents1_text.scrollTo(0, scrollAmount)
            else
                contents1_text.scrollTo(0,0)
        }
        tts = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                // 언어를 선택한다.
                tts!!.language = Locale.KOREAN
            }
        }
        val secondintent =intent
        val dataFormat1 = SimpleDateFormat("yyyy.MM.dd")

        //SharedPreferences에 값이 저장되어있지 않을 때
        if(MySharedPreferences.getUserId(this).isNullOrBlank() || MySharedPreferences.getUserPw(this).isNullOrBlank() || MySharedPreferences.getUserType(this).isNullOrBlank()) {

        }
        else {  //SharedPreferences에 값이 저장되어 있을 때
            userId = MySharedPreferences.getUserId(this)
        }
        if(intent.hasExtra("NO")) {
            boardNo = intent.getIntExtra("NO", 0)
        }
        selectGuneNo(boardNo)
        selectGuneReplyNo(boardNo)

        if(MySharedPreferences.getUserType(this).equals("0")) {
            delete_text.visibility = View.INVISIBLE
        }
        else if(MySharedPreferences.getUserType(this).equals("1")) {
            delete_text.visibility = View.VISIBLE
        }
        show_opinion.setOnClickListener({
            val intent = Intent(this, GuneOpinionActivity::class.java)
            intent.putExtra("NO", boardNo)
            startActivity(intent)
            ActivityCompat.finishAffinity(this)
            overridePendingTransition(R.anim.slide_right_enter,R.anim.slide_right_exit)
        })
        back_button.setOnClickListener({
            val intent = Intent(this, SubActivity2::class.java)
            startActivity(intent)
            ActivityCompat.finishAffinity(this)
            overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
        })
        size_up_button.setOnClickListener({
            if(size_text.text == "작게"){
                size_text.text = "중간"
                title1_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,40.0f)
                contents1_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,40.0f)
            }
            else if(size_text.text == "중간"){
                size_text.text = "크게"
                title1_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,50.0f)
                contents1_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,50.0f)
            }
        })
        size_small_button.setOnClickListener({
            if(size_text.text == "중간"){
                size_text.text = "작게"
                title1_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,30.0f)
                contents1_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,30.0f)
            }
            else if(size_text.text == "크게"){
                size_text.text = "중간"
                title1_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,40.0f)
                contents1_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40.0f)
            }
        })
        Sound_button.setOnClickListener({
            ttsSpeak(contents1_text.getText().toString())
        })
        delete_text.setOnClickListener({
            val intent = Intent(this, SubActivity2::class.java)
            var dialog = AlertDialog.Builder(this)
            var dialog1 = AlertDialog.Builder(this)
            dialog.setTitle("게시글 삭제")
            dialog.setMessage("게시글을 삭제하시겠습니까?")
            var dialog_listener = object: DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    when(which){
                        DialogInterface.BUTTON_POSITIVE -> {

                            deleteGuneNo(boardNo)

                            dialog1.setTitle("게시글 삭제 완료")
                            dialog1.setMessage("게시글을 삭제했습니다!")
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
                            dialog1.setPositiveButton("확인",dialog_listener)
                            dialog1.show()
                        }
                    }
                }
            }
            dialog.setPositiveButton("삭제하기",dialog_listener)
            dialog.setNegativeButton("취소",dialog_listener)
            dialog.show()
        })
    }

    override fun onBackPressed() {
        val intent = Intent(this, SubActivity2::class.java)
        startActivity(intent)
        ActivityCompat.finishAffinity(this)
        overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
    }

    override fun onResume() {
        super.onResume()
    }

    private fun selectGuneNo(NO: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://sejongcountry.dothome.co.kr/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(GuneInterface::class.java)
        val call: Call<String> = service.selectGuneNo(NO)
        call.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.isSuccessful && response.body() != null) {
                    var result = response.body().toString()
                    Log.d("Reg", "onResponse Success : " + response.toString())
                    Log.d("Reg", "onResponse Success : " + result)

                    val info = JSONObject(result)
                    val status = info.getString("status")

                    if(status.equals("true")) {
                        val TITLE = info.getString("TITLE")
                        val CONTENT = info.getString("CONTENT")
                        val CRTD = info.getString("CRTD")
                        val ID = info.getString("ID")

                        boardTitle = TITLE
                        boardContent = CONTENT
                        boardCrtd = CRTD

                        title1_text.text = TITLE
                        contents1_text.text = CONTENT

                        if(ID.equals(userId)) {
                            delete_text.visibility = View.VISIBLE
                        }
                    }
                    else if(status.equals("false")) {

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

    private fun selectGuneReplyNo(NO: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://sejongcountry.dothome.co.kr/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(GuneInterface::class.java)
        val call: Call<String> = service.selectGuneReplyNo(NO)
        call.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.isSuccessful && response.body() != null) {
                    var result = response.body().toString()
                    Log.d("Reg", "onResponse Success : " + response.toString())
                    Log.d("Reg", "onResponse Success : " + result)

                    val info = JSONObject(result)
                    val status = info.getString("status")

                    if(status.equals("true")) {
                        val NUM = info.getString("NUM")

                        show_opinion.text = "댓글(" + NUM + ")"
                    }
                    else if(status.equals("false")) {

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

    private fun deleteGuneNo(NO: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://sejongcountry.dothome.co.kr/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(GuneInterface::class.java)
        val call: Call<String> = service.deleteGune(NO)
        call.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
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


































