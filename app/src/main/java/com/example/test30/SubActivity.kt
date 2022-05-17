package com.example.test30

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.gongji_main.*
import kotlinx.android.synthetic.main.setting_main.*
import kotlinx.android.synthetic.main.setting_main.back_button
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.custom_list_item.*
import kotlinx.android.synthetic.main.custom_list_item.view.*
import kotlinx.android.synthetic.main.gongji_sub_main.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SubActivity : AppCompatActivity() {

    val current : Long = System.currentTimeMillis()
    val format1 = SimpleDateFormat("yyyy-MM-dd")
    var TODAY = format1.format(current)
    val items = mutableListOf<ListViewItem>()
    val adapter = ListViewAdapter(items)
    init {
        instance = this
    }
    companion object {
        lateinit var instance: SubActivity
        fun SubActivityContext(): Context {
            return instance.applicationContext
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        val currentTime : Long = System.currentTimeMillis()
        val anim_test = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.text_anim) // 글자 애니메이션

        setContentView(R.layout.gongji_main)
        val cal = Calendar.getInstance()
        cal.time = Date()

        boardListImp(0, TODAY)



        listView.adapter = adapter
        listView.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long->
            val item = parent.getItemAtPosition(position) as ListViewItem
            val intent = Intent(this, GongjiSubActivity::class.java)

            intent.putExtra("NO", adapter.getItem(position).no)
//            if(intent.hasExtra("NO")){
//                intent.getStringExtra("NO")
//            }

            startActivity(intent)
            ActivityCompat.finishAffinity(this)
            overridePendingTransition(R.anim.slide_right_enter,R.anim.slide_right_exit)
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
        select_date.setOnClickListener({
            select_date.isClickable = false
            CVOn.visibility = View.VISIBLE
            listView.visibility = View.INVISIBLE
            CVOn.startAnimation(anim_test)
            var selected_date = ""
            CalendarView.setOnDateChangeListener{view, year, month, date ->
                if((month+1 < 10) && (date < 10)) {
                    selected_date = String.format("%d-0%d-0%d",year,month+1,date)
                }
                else if((month+1 < 10) && (date >= 10)) {
                    selected_date = String.format("%d-0%d-%d",year,month+1,date)
                }
                else if((month+1 >= 10) && (date < 10)) {
                    selected_date = String.format("%d-%d-0%d",year,month+1,date)
                }
                else {
                    selected_date = String.format("%d-%d-%d",year,month+1,date)
                }

            }
            choose_date.setOnClickListener({
                select_date.isClickable = true
                CVOn.visibility = View.INVISIBLE
                listView.visibility = View.VISIBLE

                items.clear()
                boardListImp(1, selected_date)
            })
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

    private fun boardListImp(flag: Int, DAY: String) {

        val retrofit = Retrofit.Builder()
            .baseUrl("http://sejongcountry.dothome.co.kr/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(BoardInterface::class.java)
        val call: Call<String> = service.selectBoardListImp()
        call.enqueue(object: Callback<String> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                if(response.isSuccessful && response.body() != null) {
                    var result = response.body().toString()
                    Log.d("Reg", "onResponse Success : " + response.toString())
                    Log.d("Reg", "onResponse Success : " + result)

                    val info = JSONObject(result)
                    val array = info.optJSONArray("result")
                    var i =0
                    while(i < array.length()) {
                        val jsonObject = array.getJSONObject(i)
                        val NO = jsonObject.getInt("NO")
                        val TITLE = jsonObject.getString("TITLE")
                        val TYPE = jsonObject.getString("TYPE")
                        val CRTD = jsonObject.getString("CRTD")
                        val WDAY1 = LocalDateTime.parse(CRTD, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        val WDAY2 = WDAY1.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))

                        items.add(ListViewItem(ContextCompat.getDrawable(SubActivity.SubActivityContext(), R.drawable.yellow_star)!!, TITLE, WDAY2, NO))

                        i++
                    }
                    adapter.notifyDataSetChanged()

                    if(flag == 0) {
                        var TODAYD = LocalDate.parse(TODAY, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        var TODAYS = TODAYD.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        var j = 0
                        while (j < 7) {
                            boardListNor(TODAYS)
                            Thread.sleep(35L)

                            TODAYD = TODAYD.minusDays(1)
                            TODAYS = TODAYD.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            j++
                        }
                    }
                    else if(flag == 1) {
                        var TODAYD = LocalDate.parse(DAY, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        TODAYD = TODAYD.plusDays(3)
                        var TODAYS = TODAYD.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        var j = 0
                        while (j < 7) {
                            boardListNor(TODAYS)
                            Thread.sleep(30L)

                            TODAYD = TODAYD.minusDays(1)
                            TODAYS = TODAYD.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            j++
                        }
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

    private fun boardListNor(DAY: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://sejongcountry.dothome.co.kr/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(BoardInterface::class.java)
        val call: Call<String> = service.selectBoardListNor(DAY)
        call.enqueue(object: Callback<String> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                if(response.isSuccessful && response.body() != null) {
                    var result = response.body().toString()
                    Log.d("Reg", "onResponse Success : " + response.toString())
                    Log.d("Reg", "onResponse Success : " + result)

                    val info = JSONObject(result)
                    val array = info.optJSONArray("result")
                    var i =0
                    while(i < array.length()) {
                        val jsonObject = array.getJSONObject(i)
                        val NO = jsonObject.getInt("NO")
                        val TITLE = jsonObject.getString("TITLE")
                        val TYPE = jsonObject.getString("TYPE")
                        val CRTD = jsonObject.getString("CRTD")
                        val WDAY1 = LocalDateTime.parse(CRTD, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        val WDAY2 = WDAY1.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))

                        items.add(ListViewItem(ContextCompat.getDrawable(SubActivity.SubActivityContext(), R.drawable.empty_star)!!, TITLE, WDAY2, NO))

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
}

data class ListViewItem(val icon: Drawable, val title: String,val date: String, val no: Int)

class ListViewAdapter(private val items: MutableList<ListViewItem>): BaseAdapter() {
    override fun getCount(): Int = items.size
    override fun getItem(position: Int): ListViewItem = items[position]
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        var convertView = view
        if (convertView == null)
            convertView = LayoutInflater.from(parent?.context).inflate(R.layout.custom_list_item, parent, false)
        val item: ListViewItem = items[position]
        convertView!!.image_title.setImageDrawable(item.icon)
        convertView.text_title.text = item.title
        convertView.text_date.text = item.date
        return convertView
    }
}