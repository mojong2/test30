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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.create_id_main.*
import kotlinx.android.synthetic.main.setting_main.*
import kotlinx.android.synthetic.main.setting_main.back_button
import okhttp3.internal.Version
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.AccessController.getContext
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class SettingActivity : AppCompatActivity() {

    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null
    var userId = ""
    var userPw = ""
    var userToken = ""
    var hashPw = ""
    var inputPw = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.setting_main)

        //SharedPreferences에 값이 저장되어있지 않을 때
        if(MySharedPreferences.getUserId(this).isNullOrBlank() || MySharedPreferences.getUserPw(this).isNullOrBlank() || MySharedPreferences.getUserType(this).isNullOrBlank()) {

        }
        else {  //SharedPreferences에 값이 저장되어 있을 때
            userId = MySharedPreferences.getUserId(this)
            userPw = MySharedPreferences.getUserPw(this)
        }

        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if(task.isSuccessful) {
                userToken = task.result
            }
        }

        back_button.setOnClickListener({
            //            val intent = Intent(this, MainActivity2::class.java)
//            startActivity(intent)
//            ActivityCompat.finishAffinity(this)
//            overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)

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

        logout_button.setOnClickListener {
            Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
            MySharedPreferences.clearUser(this)
            logoutFireBase()
            auth?.signOut()
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
            finish()
        }
        Version_button.setOnClickListener({
            if(Current_Version_button.visibility == View.VISIBLE) Current_Version_button.visibility = View.GONE
            else Current_Version_button.visibility = View.VISIBLE
        })
//        delete_id.setOnClickListener({
//            if(password_input.visibility == View.VISIBLE) password_input.visibility = View.GONE
//            else password_input.visibility = View.VISIBLE
////            if(password_input.getText().toString().equals(pw)){
////                really_delete.visibility == View.VISIBLE
////            }
////            else really_delete.visibility == View.GONE
//            really_delete.visibility == View.VISIBLE
//        })
        delete_id.setOnClickListener({
            if(password_input.visibility == View.VISIBLE) {
                password_input.visibility = View.GONE
                really_delete.visibility = View.GONE
            }
            else {
                password_input.visibility = View.VISIBLE
                really_delete.visibility = View.VISIBLE
            }
        })
        really_delete.setOnClickListener({
            if(password_input.getText().toString().equals("") || password_input.getText().toString() == null) {
                Toast.makeText(this, "비밀번호를 입력해주세요!", Toast.LENGTH_SHORT).show()
            }
            else if(password_input.getText().length < 8) {
                Toast.makeText(this, "8자 이상 입력해주세요!", Toast.LENGTH_SHORT).show()
            }
            else {
                hashPw = getHash(password_input.getText().toString())
                if(hashPw.equals(userPw)) {
                    val intent = Intent(this, StartActivity::class.java)
                    var dialog = AlertDialog.Builder(this)
                    var dialog1 = AlertDialog.Builder(this)
                    dialog.setTitle("계정 탈퇴")
                    dialog.setMessage("계정을 탈퇴하시겠습니까?")
                    var dialog_listener = object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> { // 삭제 버튼이 눌렸음

                                    deleteUser(userId)

                                    dialog1.setTitle("계정 탈퇴 완료")
                                    dialog1.setMessage("계정을 탈퇴했습니다!")
                                    var dialog_listener = object : DialogInterface.OnClickListener {
                                        override fun onClick(dialog: DialogInterface?, which: Int) {
                                            when (which) {
                                                DialogInterface.BUTTON_POSITIVE -> {
                                                    MySharedPreferences.clearUser(this@SettingActivity)
                                                    logoutFireBase()
                                                    auth?.signOut()

                                                    startActivity(intent)
                                                    overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
                                                    finish()
                                                }
                                            }
                                        }
                                    }
                                    dialog1.setPositiveButton("확인", dialog_listener)
                                    dialog1.show()
                                }
                            }
                        }
                    }
                    dialog.setPositiveButton("탈퇴하기", dialog_listener)
                    dialog.setNegativeButton("취소", dialog_listener)
                    dialog.show()
                }
                else {
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다!", Toast.LENGTH_SHORT).show()
                }
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

    private fun logoutFireBase() {
        firestore?.collection("Information")?.document(userToken)?.delete()
    }

    private fun deleteUser(ID: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://sejongcountry.dothome.co.kr/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(RegisterInterface::class.java)
        val call: Call<String> = service.deleteUser(ID)
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
