package com.example.test30

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.gongji_main.*
import kotlinx.android.synthetic.main.opinion_list_item.view.*
import kotlinx.android.synthetic.main.gongji_opinion_main.*
import kotlinx.android.synthetic.main.gongji_opinion_main.listView
import kotlinx.android.synthetic.main.gongji_opinion_main.back_button
import kotlinx.android.synthetic.main.gongji_sub_main.*
import kotlinx.android.synthetic.main.gongji_sub_main.view.*
import kotlinx.android.synthetic.main.gune_main.*
import kotlinx.android.synthetic.main.opinion_list_item.*
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

class GongjiOpinionActivity : AppCompatActivity() {
    val current : Long = System.currentTimeMillis()
    val format2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var TODAY = format2.format(current)
    var boardNo = 0
    var replyNo = 0
    var replyContent = ""
    var replyStatus = ""
    var userId = ""
    var userType = ""
    var replyCrtd = ""
    var replyCrtu = ""
    val items = mutableListOf<ListViewItem1>()
    val adapter = ListViewAdapter1(items)
    private fun deleteOpinion(NO: Int){
        val intent = Intent(this, SubActivity::class.java)
        var dialog = AlertDialog.Builder(this)
        var dialog1 = AlertDialog.Builder(this)
        dialog.setTitle("댓글 삭제")
        dialog.setMessage("댓글을 삭제하시겠습니까?")
        var dialog_listener = object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                when(which){
                    DialogInterface.BUTTON_POSITIVE -> { // 삭제 버튼이 눌렸음
                        deleteReplyBoard(NO, boardNo)
                        dialog1.setTitle("댓글 삭제 완료")
                        dialog1.setMessage("댓글을 삭제했습니다!")
                        var dialog_listener = object: DialogInterface.OnClickListener{
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                when(which){
                                    DialogInterface.BUTTON_POSITIVE -> {

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

    }
    inner class ListViewAdapter1(private val items: MutableList<ListViewItem1>): BaseAdapter() {
        override fun getCount(): Int = items.size
        override fun getItem(position: Int): ListViewItem1 = items[position]
        override fun getItemId(position: Int): Long = position.toLong()
        override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
            var convertView = view
            if (convertView == null)
                convertView = LayoutInflater.from(parent?.context).inflate(R.layout.opinion_list_item, parent, false)
            val item: ListViewItem1 = items[position]
            convertView!!.user_name.text = item.name
            convertView.user_text.text = item.text
            convertView.user_date.text = item.date
            convertView.delete_button.isVisible = true
            if(userType.equals("0") && !(adapter.getItem(position).id.equals(userId))) {
                convertView.delete_button.isVisible = false
            }
            convertView.delete_button.setOnClickListener({
                deleteOpinion(adapter.getItem(position).no)
            })
            return convertView
        }
    }
    init {
        instance = this
    }
    companion object {
        lateinit var instance: GongjiOpinionActivity
        fun GongjiOpinionActivityContext(): Context {
            return instance.applicationContext
        }
    }
    private var tts: TextToSpeech? = null
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
        setContentView(R.layout.gongji_opinion_main)

        //SharedPreferences에 값이 저장되어있지 않을 때
        if(MySharedPreferences.getUserId(this).isNullOrBlank() || MySharedPreferences.getUserPw(this).isNullOrBlank() || MySharedPreferences.getUserType(this).isNullOrBlank()) {

        }
        else {  //SharedPreferences에 값이 저장되어 있을 때
            userId = MySharedPreferences.getUserId(this)
            userType = MySharedPreferences.getUserType(this)
        }
        if(intent.hasExtra("NO")){
            boardNo = intent.getIntExtra("NO", 0)
        }

        selectboardReplyList(boardNo)
        listView.adapter = adapter
        tts = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                // 언어를 선택한다.
                tts!!.language = Locale.KOREAN
            }
        }
        val secondintent =intent
        val dataFormat1 = SimpleDateFormat("yyyy.MM.dd")

        opinion_input.setOnClickListener({
            var replyContentInput = ""
            if(opinion_text.text.toString().equals("") || opinion_text.text == null) {
                Toast.makeText(this, "댓글을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                replyContentInput = opinion_text.text.toString()
                insertReplyBoard(boardNo, replyContentInput)
                Toast.makeText(this, "댓글을 등록했습니다.", Toast.LENGTH_SHORT).show()
                opinion_text.setText("")
                adapter.notifyDataSetChanged()
            }
        })
        back_button.setOnClickListener({
            val intent = Intent(this, GongjiSubActivity::class.java)
            intent.putExtra("NO", boardNo)
            startActivity(intent)
            ActivityCompat.finishAffinity(this)
            overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
        })
        listView.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long->
            val item = parent.getItemAtPosition(position) as ListViewItem1
            ttsSpeak(adapter.getItem(position).text)
        }
        good_text.setOnClickListener({
            insertReplyBoard(boardNo, "좋아요")
            Toast.makeText(this, "댓글을 등록했습니다.", Toast.LENGTH_SHORT).show()
        })
        okay_text.setOnClickListener({
            insertReplyBoard(boardNo, "알겠어요")
            Toast.makeText(this, "댓글을 등록했습니다.", Toast.LENGTH_SHORT).show()
        })
        question_text.setOnClickListener({
            insertReplyBoard(boardNo, "궁금해요")
            Toast.makeText(this, "댓글을 등록했습니다.", Toast.LENGTH_SHORT).show()
        })
        cool_text.setOnClickListener({
            insertReplyBoard(boardNo, "멋져요")
            Toast.makeText(this, "댓글을 등록했습니다.", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onBackPressed() {
        val intent = Intent(this, GongjiSubActivity::class.java)
        intent.putExtra("NO", boardNo)
        startActivity(intent)
        ActivityCompat.finishAffinity(this)
        overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
    }
    private fun selectboardReplyList(NO: Int) {

        val retrofit = Retrofit.Builder()
            .baseUrl("http://sejongcountry.dothome.co.kr/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(BoardInterface::class.java)
        val call: Call<String> = service.selectBoardReplyList(NO)
        call.enqueue(object: Callback<String> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                if(response.isSuccessful && response.body() != null) {
                    var result = response.body().toString()
                    Log.d("Reg", "onResponse Success : " + response.toString())
                    Log.d("Reg", "onResponse Success : " + result)

                    val info = JSONObject(result)
                    val array = info.optJSONArray("result")
                    var i = 0
                    while(i < array.length()) {
                        val jsonObject = array.getJSONObject(i)
                        val NO = jsonObject.getInt("NO")
                        val NAME = jsonObject.getString("NAME")
                        val CONTENT = jsonObject.getString("CONTENT")
                        val CRTD = jsonObject.getString("CRTD")
                        val ID = jsonObject.getString("ID")
                        val WDAY1 = LocalDateTime.parse(CRTD, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        val WDAY2 = WDAY1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

                        items.add(ListViewItem1(NAME, CONTENT, WDAY2, NO, ID))

                        i++
                    }
                    adapter.notifyDataSetChanged()
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

    private fun insertReplyBoard(NO: Int, CONTENT: String) {

        val retrofit = Retrofit.Builder()
            .baseUrl("http://sejongcountry.dothome.co.kr/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(BoardInterface::class.java)
        val call: Call<String> = service.insertReplyBoard(NO, CONTENT, userId, TODAY, userId)
        call.enqueue(object: Callback<String> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                if(response.isSuccessful && response.body() != null) {
                    var result = response.body().toString()
                    Log.d("Reg", "onResponse Success : " + response.toString())
                    Log.d("Reg", "onResponse Success : " + result)

                    items.clear()
                    selectboardReplyList(NO)
                    adapter.notifyDataSetChanged()
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

    private fun deleteReplyBoard(NO: Int, BOARDNO: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://sejongcountry.dothome.co.kr/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(BoardInterface::class.java)
        val call: Call<String> = service.deleteReplyBoard(NO)
        call.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                if(response.isSuccessful && response.body() != null) {
                    var result = response.body().toString()
                    Log.d("Reg", "onResponse Success : " + response.toString())
                    Log.d("Reg", "onResponse Success : " + result)

                    items.clear()
                    selectboardReplyList(BOARDNO)
                    adapter.notifyDataSetChanged()
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
data class ListViewItem1(val name: String, val text: String, val date: String, val no: Int, val id: String)