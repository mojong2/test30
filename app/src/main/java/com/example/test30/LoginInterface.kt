package com.example.test30

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginInterface {

    @FormUrlEncoded
    @POST("Login.php")
    fun login(
        @Field("userId") userId: String,
        @Field("userPw") userPw: String
    ): Call<String>

}