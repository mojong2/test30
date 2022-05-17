package com.example.test30

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.util.Log
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.announce_insert_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class AnnounceSpeakActivity : AppCompatActivity(){
    private var time = 0
    private var isRunning = false
    private var timerTask: Timer? = null
    private var index :Int = 1
    private lateinit var mikeanimation: AnimationDrawable
    var notificationDTO = NotificationDTO()
    var fireStorage : FirebaseStorage? = null
    var fireStore : FirebaseFirestore? = null
    var tokens = mutableListOf<String>()

    private fun initFirebase(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {

//                firebaseToken.text = task.result
                Log.d("gi",firebaseToken.text.toString())
            }
        }
    }

    private fun reset() {

        timerTask?.cancel() // timerTask가 null이 아니라면 cancel() 호출

        time = 0 // 시간저장 변수 초기화
        isRunning = false // 현재 진행중인지 판별하기 위한 Boolean변수 false 세팅
        txt_recordTime.text = "00" // 시간(초) 초기화
        txt_recordTime1.text = "00 :"
        index = 1
    }
    private fun start() {
        var min:Int = 0
        timerTask = kotlin.concurrent.timer(period = 10) {
            time++
            val sec = time / 100
            if(sec==60){
                time =0
                min++
            }
            runOnUiThread {
                if(min>=10){
                    txt_recordTime1.text = "$min"
                }
                else{
                    txt_recordTime1.text = "0"+"$min :"
                }
                if(sec>=10){
                    txt_recordTime.text = "$sec"
                }
                else{
                    txt_recordTime.text = "0"+"$sec"
                }
            }
        }
    }
    private fun pause() {
        timerTask?.cancel();
    }


    private val resetButton: Button by lazy {
        findViewById(R.id.resetButton)
    }
    private val inputButton: Button by lazy {
        findViewById(R.id.inputButton)
    }
    private val recordButton: RecordButton by lazy{
        findViewById(R.id.recordButton)
    }
    private val requiredPermissions  = arrayOf(android.Manifest.permission.RECORD_AUDIO)
    private var state = State.BEFORE_RECORDING
        set(value){
            field =value
            resetButton.isEnabled = (value == State.AFTER_RECORDING) ||
                    (value == State.ON_PLAYING)
            inputButton.isEnabled = (value == State.AFTER_RECORDING) ||
                    (value == State.ON_PLAYING)
            recordButton.updateIconWithState(value)
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.announce_insert_main)

        fireStorage = FirebaseStorage.getInstance()
        fireStore = FirebaseFirestore.getInstance()
        fireStore?.collection("Information")
            ?.get()
            ?.addOnSuccessListener { result ->
                for(document in result) {
                    val token = document["token"].toString()
                    tokens.add(token)
                }
            }

        initFirebase()
        requestAudioPermission()
        initViews()
        bindViews()
        initVariables()

        back_button.setOnClickListener({
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
            ActivityCompat.finishAffinity(this)
            overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
            stopRecording()
            finish()
        })
    }
    private fun initVariables(){
        state = State.BEFORE_RECORDING
    }
    private fun requestAudioPermission(){
        ActivityCompat.requestPermissions(this, requiredPermissions,201)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //권한이 부여받은게 맞는지 check 권한부여받았으면 true 아니면 false
        val audioRequestPermissionGranted =
            requestCode == REQUEST_RECORD_AUDIO_PERMISSION &&
                    grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED

        //권한이 부여되지않으면 어플 종료
        if(!audioRequestPermissionGranted){
            finish()
        }
    }

    private fun initViews() {
        recordButton.updateIconWithState(state)
    }

    companion object{
        //permission code 선언
        private const val REQUEST_RECORD_AUDIO_PERMISSION =201
    }

    private var recorder:MediaRecorder? = null
    private var player: MediaPlayer? = null
    private val recordingFilePath :String by lazy {
        "${externalCacheDir?.absolutePath}/recording.3gp"
        //"/storage/emulated/0/Android/data/com.example.test30/Music"
    }

    private fun startRecording(){
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(recordingFilePath)
            prepare()
        }
        recorder?.start()
        state = State.ON_RECORDING
    }
    private fun stopRecording(){
        recorder?.run{
            stop()
            release()
        }
        recorder =null
        state = State.AFTER_RECORDING
    }
    private fun startPlaying() {
        player = MediaPlayer().apply {
            setDataSource(recordingFilePath)
            prepare()
        }
        player?.isLooping = true
        player?.start()
        state = State.ON_PLAYING

    }
    private fun stopPlaying(){
        player?.release()
        player =null
        state = State.AFTER_RECORDING
    }
    private fun bindViews(){
        val mikeImage = findViewById<ImageView>(R.id.MikeBtn).apply {
            setBackgroundResource(R.drawable.record_anim)
            mikeanimation = background as AnimationDrawable
        }
        resetButton.setOnClickListener {
            stopPlaying()
            reset()
            state =State.BEFORE_RECORDING
        }
        recordButton.setOnClickListener{
            when(state){
                State.BEFORE_RECORDING->{
                    mikeanimation.start()
                    start()
                    startRecording()
                }
                State.ON_RECORDING->{
                    mikeanimation.stop()
                    pause()
                    stopRecording()
                }
                State.AFTER_RECORDING->{
                    startPlaying()
                }
                State.ON_PLAYING->{
                    stopPlaying()
                }
            }
        }
        inputButton.setOnClickListener {
            var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            var fileName = "NOTI_" + timeStamp + ".mp3"
            var storageRef = fireStorage?.reference?.child("Notification")?.child(fileName)
            val stream = FileInputStream(File(recordingFilePath))

            var file: Uri? = Uri.fromFile(File(recordingFilePath)) //
            storageRef?.putFile(file!!)?.addOnSuccessListener {
                //storageRef?.putStream(stream)?.addOnSuccessListener {
                notificationDTO.name = fileName
                notificationDTO.date = SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분").format(Date())
                fireStore?.collection("Notification")?.document(fileName)?.set(notificationDTO)

                //알림 보내기
                for (token in tokens) {
                    sendPushPost(token, "녹음 공지사항이 등록되었습니다!")
                }

                Toast.makeText(this, "녹음 공지사항이 등록되었습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity3::class.java)
                startActivity(intent)
                ActivityCompat.finishAffinity(this)
                overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
            }?.addOnFailureListener {
                Toast.makeText(this, "녹음 공지사항 등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity3::class.java)
                startActivity(intent)
                ActivityCompat.finishAffinity(this)
                overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
            }
        }
    }

    private fun sendPushPost(TOKEN: String, TYPE: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://sejongcountry.dothome.co.kr/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(FcmInterface::class.java)
        val call: Call<String> = service.sendPush(TOKEN, TYPE, notificationDTO.date!!)
        call.enqueue(object : Callback<String> {
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

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity3::class.java)
        startActivity(intent)
        ActivityCompat.finishAffinity(this)
        overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
        stopRecording()
        finish()
    }
}
