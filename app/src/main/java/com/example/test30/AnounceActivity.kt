package com.example.test30

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.announce_list_item.*
import kotlinx.android.synthetic.main.announce_list_item.view.*
import kotlinx.android.synthetic.main.anounce_main.*
import kotlinx.android.synthetic.main.setting_main.*
import kotlinx.android.synthetic.main.setting_main.back_button
import java.io.File

class AnounceActivity : AppCompatActivity() {

    val items = mutableListOf<ListViewItem4>()
    val adapter = ListViewAdapter4(items)
    val fireStore = FirebaseFirestore.getInstance()
    val fireStorage = FirebaseStorage.getInstance()
    var mediaPlayer : MediaPlayer? = null

    private fun playAnnounce(NAME: String) {
        var storageRef = fireStorage?.reference?.child("Notification")?.child(NAME)

        mediaPlayer = MediaPlayer()

        storageRef!!.downloadUrl.addOnSuccessListener {
            mediaPlayer = MediaPlayer.create(this, it)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        }
    }
    private fun stopAnnounce() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
    inner class ListViewAdapter4(private val items: MutableList<ListViewItem4>): BaseAdapter() {
        override fun getCount(): Int = items.size
        override fun getItem(position: Int): ListViewItem4 = items[position]
        override fun getItemId(position: Int): Long = position.toLong()
        override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
            var convertView = view
            if(convertView == null)
                convertView = LayoutInflater.from(parent?.context).inflate(R.layout.announce_list_item, parent, false)
            val item: ListViewItem4 = items[position]
            convertView!!.announce_date.text = item.date
            convertView.announce_button.isVisible = true
            convertView.announce_button.setImageResource(R.drawable.ic_play)
            convertView.announce_button.setOnClickListener({
                if(adapter.getItem(position).state == 0) {
                    if(mediaPlayer != null) {
                        Toast.makeText(this@AnounceActivity, "진행중인 방송을 종료해주세요!", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        this.items.set(position, ListViewItem4(adapter.getItem(position).name, adapter.getItem(position).date, 1))
                        convertView.announce_button.setImageResource(R.drawable.ic_stop)
                        playAnnounce(adapter.getItem(position).name)
                    }
                }
                else if(adapter.getItem(position).state == 1) {
                    this.items.set(position, ListViewItem4(adapter.getItem(position).name, adapter.getItem(position).date, 0))
                    convertView.announce_button.setImageResource(R.drawable.ic_play)
                    stopAnnounce()
                }
            })
            return convertView
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.anounce_main)

        listView.adapter = adapter
        fireStore.collection("Notification")
            .get()
            .addOnSuccessListener { result->
                for(document in result) {
                    val date = document["date"].toString()
                    val name = document["name"].toString()
                    items.add(ListViewItem4(name, date, 0))
                    adapter.notifyDataSetChanged()
                }
            }

        back_button.setOnClickListener({
            stopAnnounce()
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
    }
    override fun onBackPressed() {
        stopAnnounce()
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

}
data class ListViewItem4(val name: String, val date: String, val state: Int)