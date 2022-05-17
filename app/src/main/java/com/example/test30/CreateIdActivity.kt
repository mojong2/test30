package com.example.test30

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.create_id_main.*
import org.json.JSONException
import org.json.JSONObject
import org.mindrot.jbcrypt.BCrypt
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import javax.crypto.Cipher.SECRET_KEY
import javax.crypto.spec.SecretKeySpec


class CreateIdActivity : AppCompatActivity() {

    private var auth : FirebaseAuth? = null
    init {
        instance = this
    }
    companion object {
        lateinit var instance: CreateIdActivity
        fun CreateIdActivityContext(): Context {
            return instance.applicationContext
        }
    }

    var userId = "" // 아이디 임시 저장
    var userPw = "" // 비밀번호 임시 저장
    var userBirth = "" // 생년월일 임시 저장
    var userGender = 1 // 성별 저장
    var userName = "" // 이름 임시 저장
    var userGuardianphone = "" // 보호자 번호 임시 저장
    var userPhone= "" // 본인 번호 임시 저장
    var userTelephone = "" // 집 전화번호 임시 저장
    var userAddress = "" // 주소 임시 저장
    var hashPw = ""
    var recong = 0 // 아이디 인증 받으면 1로 바뀜
    //    private var preferenceHelper: PreferenceHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.create_id_main)
        val anim_test = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.text_anim) // 글자 애니메이션
        var authNum = "1234" // 인증 번호
        var sameId = "bigbang123" // 임시로 존재한다고 생각하고 만든 닉네임임
        var seq = 0 // 순서
//        preferenceHelper = PreferenceHelper(this)

        auth = Firebase.auth

        // 뒤로가기 버튼이 눌렸을때
        back_button.setOnClickListener({
            when(seq){ // 코틀린 switch 버젼
                0->{//순서가 처음인 경우에는 메인화면으로 돌아간다.
                    val intent = Intent(this, StartActivity::class.java) // 로그인 화면으로 변신
                    startActivity(intent)
                    ActivityCompat.finishAffinity(this)
                    overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
                }
                1->{//순서가 1인 경우에는 아이디입력으로 돌아간다.
                    main_menu.text = "아이디 입력"
                    id_input.setHint("예: jeonju123")
                    ex_main.text = "5~20자의 영어 소문자, 숫자로 입력해주세요"
                    id_input.visibility = View.VISIBLE
                    pw_input.visibility = View.INVISIBLE
                    checkId.visibility = View.VISIBLE
                    seq--
                    recong  = 0
                    main_menu.startAnimation(anim_test)
                    ex_main.startAnimation(anim_test)
                    id_input.startAnimation(anim_test)
                }
                2->{//순서가 2인 경우에는 비밀번호입력으로 돌아간다.
                    main_menu.text = "비밀번호 입력"
                    pw_input.setHint("비밀번호를 입력하세요")
                    ex_main.text = "8자 이상 입력해주세요"
                    seq--
                    main_menu.startAnimation(anim_test)
                    ex_main.startAnimation(anim_test)
                }
                3->{//순서가 3인 경우에는 재비밀번호입력으로 돌아간다.
                    pw_input.visibility = View.VISIBLE
                    all_input.visibility = View.INVISIBLE
                    main_menu.text = "비밀번호 재입력"
                    pw_input.setHint("비밀번호를 입력하세요")
                    ex_main.text = "8자 이상 입력해주세요"
                    seq--
                    main_menu.startAnimation(anim_test)
                    ex_main.startAnimation(anim_test)
                    pw_input.startAnimation(anim_test)
                }
                4->{//순서가 4인 경우에는 이름입력으로 돌아간다.
                    all_input.visibility = View.VISIBLE
                    birth_input.visibility = View.INVISIBLE
                    sex_input.visibility = View.INVISIBLE
                    main_menu.text = "이름 입력"
                    all_input.setHint("이름을 입력하세요")
                    ex_main.setText(null)
                    seq--
                    main_menu.startAnimation(anim_test)
                    ex_main.startAnimation(anim_test)
                    all_input.startAnimation(anim_test)
                }
                5->{//순서가 5인 경우에는 생년월일입력으로 돌아간다.
                    birth_input.visibility = View.VISIBLE
                    sex_input.visibility = View.VISIBLE
                    number_input.visibility = View.INVISIBLE
                    main_menu.text = "생년월일 입력"
                    ex_main.setText(null)
                    seq--
                    main_menu.startAnimation(anim_test)
                    sex_input.startAnimation(anim_test)
                    birth_input.startAnimation(anim_test)
                }
                6->{//순서가 6인 경우에는 보호자 전화번호 입력으로 돌아간다.
                    main_menu.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40.0f)
                    main_menu.text = "보호자 휴대전화 입력"
                    ex_main.setText(null)
                    seq--
                    main_menu.startAnimation(anim_test)
                    number_input.startAnimation(anim_test)
                }
                7->{//순서가 7인 경우에는 집 전화번호 입력으로 돌아간다.
                    main_menu.text = "집 전화번호 입력"
                    all_input.visibility = View.INVISIBLE
                    number_input.visibility = View.VISIBLE
                    ex_main.setText(null)
                    seq--
                    main_menu.startAnimation(anim_test)
                    ex_main.startAnimation(anim_test)
                    number_input.startAnimation(anim_test)
                }
                8->{//순서가 8인 경우에는 주소 입력으로 돌아간다.
                    main_menu.text = "주소 입력"
                    all_input.setHint("예: 전북 전주시 전주구 전주길 452")
                    all_input.visibility = View.VISIBLE
                    number_input.visibility = View.INVISIBLE
                    ex_main.setText(null)
                    seq--
                    recong  = 0
                    first_input.isFocusableInTouchMode = true
                    first_input.isFocusable = true
                    second_input.isFocusableInTouchMode = true
                    second_input.isFocusable = true
                    third_input.isFocusableInTouchMode = true
                    third_input.isFocusable = true
                    checkPn.visibility = View.INVISIBLE
                    main_menu.startAnimation(anim_test)
                    ex_main.startAnimation(anim_test)
                    all_input.startAnimation(anim_test)
                }
                9->{//순서가 9인 경우에는 본인 전화번호 입력으로 돌아간다.
                    main_menu.text = "본인 휴대전화 입력"
                    all_input.visibility = View.INVISIBLE
                    number_input.visibility = View.VISIBLE
                    ex_main.setText(null)
                    seq--
                    recong  = 0
                    first_input.isFocusableInTouchMode = true
                    first_input.isFocusable = true
                    second_input.isFocusableInTouchMode = true
                    second_input.isFocusable = true
                    third_input.isFocusableInTouchMode = true
                    third_input.isFocusable = true
                    main_menu.startAnimation(anim_test)
                    ex_main.startAnimation(anim_test)
                    number_input.startAnimation(anim_test)
                }
            }
        })
        //남자버튼이 눌렸을때
        manCheck.setOnClickListener({
            womanCheck.isChecked = false
        })
        //여자버튼이 눌렸을때
        womanCheck.setOnClickListener({
            manCheck.isChecked = false
        })
        //아이디 중복 확인 버튼이 눌렸을때
        checkId.setOnClickListener({

            checkMe(id_input.getText().toString())

//            var check = ""
//            check = checkMe(id_input.getText().toString())

//            if(id_input.getText().toString().equals("") || all_input.getText().toString() == null){
//                ex_main.text = "아이디를 입력해주세요."
//            }
//            else if(id_input.getText().length < 7){
//                ex_main.text = "8자 이상 입력해주세요."
//            }
//            else if(check.equals("false")){
//                ex_main.text = "이미 사용중인 아이디입니다."
//            }
//            else if(check.equals("true")) {
//                recong = 1
//                userId = id_input.getText().toString()
//                ex_main.text = "사용가능한 아이디입니다."
//                id_input.isClickable = false
//                id_input.isFocusable = false
//            }
//            else {
//
//            }
        })

        checkPn.setOnClickListener({

            if((first_input.getText().toString().equals("") || first_input.getText()
                    .toString() == null || second_input.getText().toString().equals("") || second_input.getText()
                    .toString() == null || third_input.getText().toString().equals("") || third_input.getText()
                    .toString() == null)) {
                ex_main.setText("휴대폰 번호를 입력해 주세요.")
            }
            else {
                userPhone = first_input.getText().toString() + "-" + second_input.getText().toString() + "-" + third_input.getText().toString()
                checkMePhone(userPhone)
            }

        })

        //다음단계를 눌렀을때
        next_button.setOnClickListener({
            when (seq) {
                0 -> {//순서가 맨처음인 경우에는 아이디를 확인 후 넘어간다.
                    if (recong == 0) ex_main.text = "아이디 인증을 해주세요"
                    else {//성공할 경우 비밀번호 입력으로 간다.
                        main_menu.text = "비밀번호 입력"
                        checkId.visibility = View.INVISIBLE
                        id_input.visibility = View.INVISIBLE
                        pw_input.visibility = View.VISIBLE
                        id_input.setText(null)
                        id_input.setHint(null)
                        ex_main.text = "8자 이상 입력해주세요"
                        seq++
                        recong = 0
                        main_menu.startAnimation(anim_test)
                        pw_input.startAnimation(anim_test)
                        id_input.isFocusableInTouchMode = true
                        id_input.isFocusable = true
                    }
                }
                1 -> {//순서가 1인 경우에는 비밀번호를 확인 후 넘어간다.
                    if (pw_input.getText().toString().equals("") || pw_input.getText()
                            .toString() == null
                    ) ex_main.text ="비밀번호를 입력하세요"
                    else if (pw_input.getText().length < 8) ex_main.text = "비밀번호를 8자 이상 입력하세요"
                    else {
                        main_menu.text = "비밀번호 재입력"
                        userPw = pw_input.getText().toString()
                        ex_main.text = "8자 이상 입력해주세요"
                        pw_input.setText(null)
                        seq++
                        main_menu.startAnimation(anim_test)
                        pw_input.startAnimation(anim_test)
                    }
                }
                2 -> {//순서가 2인 경우에는 비밀번호 재확인 후 넘어간다.
                    if (pw_input.getText().toString().equals("") || pw_input.getText()
                            .toString() == null
                    ) ex_main.text ="비밀번호를 입력하세요"
                    else if(pw_input.getText().toString().equals(userPw) ) {
                        main_menu.text = "이름 입력"
                        pw_input.setText(null)
                        all_input.setHint("이름을 입력하세요")
                        ex_main.setText(null)
                        pw_input.visibility = View.INVISIBLE
                        all_input.visibility = View.VISIBLE
                        seq++
                        main_menu.startAnimation(anim_test)
                        all_input.startAnimation(anim_test)
                    }
                    else ex_main.text ="비밀번호가 다릅니다"
                }
                3 -> {//순서가 3인 경우에는 이름을 확인 후 넘어간다.
                    if (all_input.getText().toString().equals("") || all_input.getText()
                            .toString() == null
                    ) ex_main.setText("이름을 입력하세요")
                    else {
                        main_menu.text = "생년월일 입력"
                        birth_input.visibility = View.VISIBLE
                        sex_input.visibility = View.VISIBLE
                        all_input.visibility = View.INVISIBLE
                        main_menu.text = "생년월일 입력"
                        ex_main.setText(null)
                        userName = all_input.getText().toString()
                        all_input.setText(null)
                        seq++
                        main_menu.startAnimation(anim_test)
                        sex_input.startAnimation(anim_test)
                        birth_input.startAnimation(anim_test)
                    }
                }
                4 -> {//순서가 4인 경우에는 생년월일을 확인 후 넘어간다.
                    if (year_input.getText().toString().equals("") || year_input.getText()
                            .toString() == null || month_input.getText().toString()
                            .equals("") || month_input.getText()
                            .toString() == null || day_input.getText().toString()
                            .equals("") || day_input.getText().toString() == null || (womanCheck.isChecked == false && manCheck.isChecked == false)
                    ) ex_main.setText("생년월일, 성별을 입력하세요")
                    else {
                        if(womanCheck.isChecked == true) userGender = 0
                        else userGender = 1
                        main_menu.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40.0f)
                        userBirth =
                            year_input.getText().toString() + "-" + month_input.getText().toString() + "-" + day_input.getText().toString()
                        year_input.setText(null)
                        month_input.setText(null)
                        day_input.setText(null)
                        womanCheck.isChecked = false
                        manCheck.isChecked = false
                        ex_main.setText(null)
                        birth_input.visibility = View.INVISIBLE
                        sex_input.visibility = View.INVISIBLE
                        number_input.visibility = View.VISIBLE
                        main_menu.text = "보호자 휴대전화 입력"
                        seq++
                        main_menu.startAnimation(anim_test)
                        number_input.startAnimation(anim_test)
                    }
                }
                5 -> {//순서가 5인 경우에는 보호자 휴대전화를 확인 후 넘어간다.
                    if (first_input.getText().toString().equals("") || first_input.getText()
                            .toString() == null || second_input.getText().toString().equals("") || second_input.getText()
                            .toString() == null || third_input.getText().toString().equals("") || third_input.getText()
                            .toString() == null
                    ) ex_main.setText("보호자 휴대전화를 입력하세요")
                    else {
                        main_menu.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 50.0f)
                        main_menu.text = "집 전화번호 입력"
                        userGuardianphone = first_input.getText().toString() + "-" + second_input.getText().toString() + "-" + third_input.getText().toString()
                        first_input.setText(null)
                        second_input.setText(null)
                        third_input.setText(null)
                        seq++
                        main_menu.startAnimation(anim_test)
                        number_input.startAnimation(anim_test)
                    }
                }
                6 -> {//순서가 6인 경우에는 집 전화번호를 확인 후 넘어간다.
                    if (first_input.getText().toString().equals("") || first_input.getText()
                            .toString() == null || second_input.getText().toString().equals("") || second_input.getText()
                            .toString() == null || third_input.getText().toString().equals("") || third_input.getText()
                            .toString() == null
                    )ex_main.setText("집 전화번호를 입력하세요")
                    else {
                        main_menu.text = "주소 입력"
                        userTelephone = first_input.getText().toString() + "-" + second_input.getText().toString() + "-" + third_input.getText().toString()
                        all_input.visibility = View.VISIBLE
                        number_input.visibility= View.INVISIBLE
                        first_input.setText(null)
                        second_input.setText(null)
                        third_input.setText(null)
                        all_input.setText(null)
                        ex_main.setText(null)
                        all_input.setHint("예: 전북 전주시 전주구 전주길 452")
                        seq++
                        main_menu.startAnimation(anim_test)
                        all_input.startAnimation(anim_test)
                    }
                }
                7 -> {//순서가 7인 경우에는 주소를 확인 후 넘어간다.
                    if (all_input.getText().toString().equals("") || all_input.getText()
                            .toString() == null
                    ) ex_main.setText("주소를 입력하세요")
                    else {
                        main_menu.text = "본인 휴대전화 입력"
                        all_input.visibility = View.INVISIBLE
                        number_input.visibility= View.VISIBLE
                        checkPn.visibility = View.VISIBLE
                        userAddress = all_input.getText().toString()
                        ex_main.setText(null)
                        all_input.setText(null)
                        seq++
                        main_menu.startAnimation(anim_test)
                        number_input.startAnimation(anim_test)
                    }
                }
                8 -> {//순서가 8인 경우에는 본인 휴대전화를 확인 후 넘어간다.
                    if (recong == 0)ex_main.setText("휴대폰 번호를 인증받아 주세요.")
                    else {
                        main_menu.text = "인증번호 입력"
                        userPhone = first_input.getText().toString() + "-" + second_input.getText().toString() + "-" + third_input.getText().toString()
                        all_input.visibility = View.VISIBLE
                        number_input.visibility= View.INVISIBLE
                        first_input.setText(null)
                        second_input.setText(null)
                        third_input.setText(null)
                        all_input.setText(null)
                        all_input.setHint(null)
                        ex_main.setText(null)
                        recong = 0
                        seq++
                        main_menu.startAnimation(anim_test)
                        all_input.startAnimation(anim_test)
                    }
                }
                9 -> {//순서가 9인 경우에는 인증번호를 확인 후 넘어간다.
                    val intent = Intent(this, MainActivity::class.java) // 로그인 화면으로 변신
                    if (all_input.getText().toString().equals("") || all_input.getText().toString() == null)
                        ex_main.setText("인증번호를 입력하세요")
                    else if(all_input.getText().toString().equals(authNum)){
                        /////////////////////////////////////////////////////
                        hashPw = getHash(userPw)
                        registerMe()
                        var tempId = userId + "@gmail.com"
                        auth?.createUserWithEmailAndPassword(tempId, hashPw)
                        /////////////////////////////////////////////////////

                        var dialog = AlertDialog.Builder(this)
                        dialog.setTitle("회원가입 성공!")
                        dialog.setMessage("성공적으로 회원가입을 하셨습니다!")
                        var dialog_listener = object: DialogInterface.OnClickListener{
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                when(which){
                                    DialogInterface.BUTTON_POSITIVE -> {
                                        intent.putExtra("name",userId)
                                        intent.putExtra("pw",userPw)
                                        startActivity(intent)
                                    }
                                }
                            }
                        }
                        dialog.setPositiveButton("처음 화면으로 가기",dialog_listener)
                        dialog.show()
                    }
                    else Toast.makeText(applicationContext, "인증번호가 틀립니다", Toast.LENGTH_SHORT)
                }
            }
        })
    }
    override fun onBackPressed(){
        val intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
        ActivityCompat.finishAffinity(this)
        overridePendingTransition(R.anim.slide_left_enter,R.anim.slide_left_exit)
    }

    //////////////////////////////////////////////////////////////////////////////////

    private fun registerMe() {
        var ID: String = userId
        var PW: String = hashPw
        var NAME: String = userName
        var GENDER: String = ""
        var BIRTH: String = userBirth
        var PHONE: String = userPhone
        var TELEPHONE: String = userTelephone
        var GUARDIANPHONE: String = userGuardianphone
        var ADDRESS: String = userAddress
        var CRTD: String = ""
        var CRTU: String = userId

        if(userGender == 1) {
            GENDER = "m"
        }
        else {
            GENDER = "w"
        }
        val current : Long = System.currentTimeMillis()
        val format1 = SimpleDateFormat("yyyy-MM-dd")
        CRTD = format1.format(current)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://sejongcountry.dothome.co.kr/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RegisterInterface::class.java)
        val call: Call<String> = api.getUserRegist(ID, PW, NAME, GENDER, BIRTH, PHONE, TELEPHONE, GUARDIANPHONE, ADDRESS, CRTD, CRTU)
        call.enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                if(response.isSuccessful && response.body() != null) { //통신성공
                    var result: String? = response.body()
                    Log.d("Reg", "onResponse Success : " + result)

//                    val jsonResponse: String? = result
//                    try {
//                        parseRegData(jsonResponse)
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
                }
                else {  //통신실패
                    Log.d("Reg", "onResponse Failed : ")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("Reg", "error : " + t.message.toString())
            }
        })
    }

//    @Throws(JSONException::class)
//    private fun parseRegData(response: String?) {
//        val jsonObject = JSONObject(response)
//        if (jsonObject.optString("status") == "true") {
//            saveInfo(response)
//            Toast.makeText(this@CreateIdActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(this@CreateIdActivity, jsonObject.getString("message"), Toast.LENGTH_SHORT)
//                .show()
//        }
//    }
//
//    private fun saveInfo(response: String?) {
//        preferenceHelper?.putIsLogin(true)
//        try {
//            val jsonObject = JSONObject(response)
//            if (jsonObject.getString("status") == "true") {
//                val dataArray = jsonObject.getJSONArray("data")
//                for (i in 0 until dataArray.length()) {
//                    val dataobj = dataArray.getJSONObject(i)
//                    preferenceHelper?.putName(dataobj.getString("name"))
//                    preferenceHelper?.putHobby(dataobj.getString("hobby"))
//                }
//            }
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//    }

    //////////////////////////////////////////////////////////////////////////////////

    private fun checkMe(ID: String) {
        //var ID: String = userId

        val retrofit = Retrofit.Builder()
            .baseUrl("http://sejongcountry.dothome.co.kr/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RegisterInterface::class.java)
//        val call: Call<UserGetDTO> = api.checkIdDup(ID)
//        var result: String? = null
//        call.enqueue(object : Callback<UserGetDTO>{
//            override fun onResponse(call: Call<UserGetDTO>, response: retrofit2.Response<UserGetDTO>) {
//                if(response.isSuccessful && response.body() != null) { //통신성공
//                    result = response.body().toString()
//                    Log.d("Reg", "onResponse Success : " + response.toString())
//                    Log.d("Reg", "onResponse Success : " + result)
//                }
//                else {  //통신실패
//                    Log.d("Reg", "onResponse Failed : ")
//                }
//            }
//
//            override fun onFailure(call: Call<UserGetDTO>, t: Throwable) {
//                Log.d("Reg", "error : " + t.message.toString())
//            }
//        })
        ////////////////////////////////////////////
        val call: Call<String> = api.checkIdDup(ID)
        call.enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                if(response.isSuccessful && response.body() != null) { //통신성공
                    var result = response.body().toString()
                    Log.d("Reg", "onResponse Success : " + response.toString())
                    Log.d("Reg", "onResponse Success : " + result)

                    val info = JSONObject(result)
                    val status = info.getString("status")

                    if(id_input.getText().toString().equals("") || all_input.getText().toString() == null){
                        ex_main.text = "아이디를 입력해주세요."
                    }
                    else if(id_input.getText().length < 8){
                        ex_main.text = "8자 이상 입력해주세요."
                    }
                    else if(status.equals("false")){
                        ex_main.text = "이미 사용중인 아이디입니다."
                    }
                    else if(status.equals("true")) {
                        recong = 1
                        userId = id_input.getText().toString()
                        ex_main.text = "사용가능한 아이디입니다."
                        id_input.isClickable = false
                        id_input.isFocusable = false
                    }
                    else {

                    }
                }
                else {  //통신실패
                    Log.d("Reg", "onResponse Failed : ")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("Reg", "error : " + t.message.toString())
            }
        })
        //////////////////////////////////////////

//        if(result != null) {
//
//            val info = JSONObject(result)
//            val status = info.getString("status")
//
//            if(status.equals("true")) {
//                return "true"
//            }
//            else {
//                return "false"
//            }
//        }
//        else {
//            return "false"
//        }
    }

    private fun checkMePhone(PHONE: String) {

        val retrofit = Retrofit.Builder()
            .baseUrl("http://sejongcountry.dothome.co.kr/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RegisterInterface::class.java)
        val call: Call<String> = api.checkPhoneDup(PHONE)
        call.enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                if(response.isSuccessful && response.body() != null) { //통신성공
                    var result = response.body().toString()
                    Log.d("Reg", "onResponse Success : " + response.toString())
                    Log.d("Reg", "onResponse Success : " + result)

                    val info = JSONObject(result)
                    val status = info.getString("status")

                    if(status.equals("false")){
                        ex_main.text = "이미 사용중인 전화번호입니다."
                    }
                    else if(status.equals("true")) {
                        recong = 1
                        ex_main.text = "사용가능한 전화번호입니다."
                        first_input.isClickable = false
                        first_input.isFocusable = false
                        second_input.isClickable = false
                        second_input.isFocusable = false
                        third_input.isClickable = false
                        third_input.isFocusable = false
                    }
                    else {

                    }
                }
                else {  //통신실패
                    Log.d("Reg", "onResponse Failed : ")
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