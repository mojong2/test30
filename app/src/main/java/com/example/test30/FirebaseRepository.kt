package com.example.test30

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.ResponseBody
import retrofit2.Response

class FirebaseRepository {

    val userDTO = MutableLiveData<UserDTO>()
    val myResponse : MutableLiveData<Response<ResponseBody>> = MutableLiveData()

    private val fireStore = FirebaseFirestore.getInstance()

    fun profileLoad(uid : String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task->
            if(!task.isSuccessful) { //실패
                Log.d("TAG", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result

            fireStore.collection("users").document(uid)
                .addSnapshotListener { documentSnapshot, _ ->
                    if(documentSnapshot == null) return@addSnapshotListener

                    val userDTO = documentSnapshot.toObject(UserDTO::class.java)
                    if (userDTO?.userId != null) {

                        // 토큰이 변경되었을 경우 갱신
                        if(userDTO.token != token){
                            Log.d("TAG", "profileLoad: 토큰 변경되었음.")
                            val newUserDTO = UserDTO(userDTO.uId,userDTO.userId, token)
                            fireStore.collection("users").document(uid).set(newUserDTO)

                            // 유저정보 라이브데이터 변경하기
                            this.userDTO.value = newUserDTO
                        }

                        // 아니면 그냥 불러옴
                        else {
                            Log.d("TAG", "profileLoad: 이미 동일한 토큰이 존재함.")
                            this.userDTO.value = userDTO!!
                        }
                    }

                    // 아이디 최초 생성 시
                    else if(userDTO?.userId == null) {
                        Log.d("TAG", "아이디가 존재하지 않음")
                        val newUserDTO = UserDTO(uid, "사용자", token)
                        fireStore.collection("users").document(uid).set(newUserDTO)

                        this.userDTO.value = newUserDTO
                    }
                }
        }
    }

    suspend fun sendNotification(notification: NotificationBody) {
        myResponse.value = RetrofitInstance.api.sendNotification(notification)
    }

}