package com.example.test30

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
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
import com.example.test30.MyFirebaseMessagingService.Companion.token
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.android.synthetic.main.gongji_insert_main.*
import kotlinx.android.synthetic.main.gongji_insert_main.back_button
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class GongjiInsertActivity : AppCompatActivity() {

    private val channelID = "com.example.test30.channel1"
    private var notificationManager : NotificationManager? = null
    private val KEY_REPLY = "key_reply"
    val firestore = FirebaseFirestore.getInstance()
    var tokens = mutableListOf<String>()

    private val fireStore = FirebaseFirestore.getInstance()

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

        setContentView(R.layout.gongji_insert_main)

        today_text.text = "작성일자 : " + TODAY

        //SharedPreferences에 값이 저장되어있지 않을 때
        if(MySharedPreferences.getUserId(this).isNullOrBlank() || MySharedPreferences.getUserPw(this).isNullOrBlank() || MySharedPreferences.getUserType(this).isNullOrBlank()) {

        }
        else {  //SharedPreferences에 값이 저장되어 있을 때
            userId = MySharedPreferences.getUserId(this)
        }

        firestore.collection("Information")
            .get()
            .addOnSuccessListener { result ->
                for(document in result) {
                    val token = document["token"].toString()
                    tokens.add(token)
                }
            }

        back_button.setOnClickListener({
            if(MySharedPreferences.getUserType(this).equals("0")) {
                val intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)
                ActivityCompat.finishAffinity(this)
                overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
                finish()
            }
            else if(MySharedPreferences.getUserType(this).equals("1")) {
                val intent = Intent(this, MainActivity3::class.java)
                startActivity(intent)
                ActivityCompat.finishAffinity(this)
                overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
                finish()
            }
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
                boardStatus = "u"
                boardCrtu = userId
                boardCrtd = format2.format(current)
                insertBoardPost(boardTitle, boardContent, boardType, boardStatus, userId, boardCrtu, boardCrtd)

                //채널 생성
//                notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                createNotificationChannel(channelID, "새 알림", "새로운 공지사항이 등록되었습니다.")
//                displayNotification()

                var message = ""
                if(boardType.equals("1")) {
                    message = "중요 공지사항이 등록되었습니다!"
                }
                else if (boardType.equals("0")) {
                    message = "새로운 공지사항이 등록되었습니다!"
                }
                for(token in tokens) {
                    sendPushPost(token, message)
                    Log.d("token", token)
                }

                Toast.makeText(this, "게시글이 등록되었습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity3::class.java)
                startActivity(intent)
                ActivityCompat.finishAffinity(this)
                overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
            }
        })
    }
    override fun onBackPressed() {
        if(MySharedPreferences.getUserType(this).equals("0")) {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
            ActivityCompat.finishAffinity(this)
            overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
            finish()
        }
        else if(MySharedPreferences.getUserType(this).equals("1")) {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
            ActivityCompat.finishAffinity(this)
            overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
            finish()
        }
    }

    private fun insertBoardPost(TITLE: String, CONTENT: String, TYPE: String, STATUS: String, ID: String, CRTU: String, CRTD: String) {

        val retrofit = Retrofit.Builder()
            .baseUrl("http://sejongcountry.dothome.co.kr/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(BoardInterface::class.java)
        val call: Call<String> = service.insertBoard(TITLE, CONTENT, TYPE, STATUS, ID, CRTU, CRTD)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
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

    private fun sendPushPost(TOKEN: String, TYPE: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://sejongcountry.dothome.co.kr/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(FcmInterface::class.java)
        val call: Call<String> = service.sendPush(TOKEN, TYPE, boardTitle)
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

    //채널 만들기 및 중요도 설정
    private fun createNotificationChannel(id: String, name: String, channelDescription: String) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(id, name, importance).apply {
                description = channelDescription
            }
            notificationManager?.createNotificationChannel(channel)
        }
        else {

        }
    }

    //알림 보내기
    private fun displayNotification() {
        val notificationId = 45
        val tapResultIntent = Intent(this, SubActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            tapResultIntent,
            PendingIntent.FLAG_MUTABLE
        )
        val notification: Notification = NotificationCompat.Builder(this@GongjiInsertActivity, channelID)
            .setContentTitle("새 알림")
            .setContentText("새로운 공지사항이 등록되었습니다.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager?.notify(notificationId, notification)
    }

}