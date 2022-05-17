package com.example.test30

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface FindInterface {

    @FormUrlEncoded
    @POST("FindId.php")
    fun findId(
        @Field("userName") userName: String,
        @Field("userPhone") userPhone: String
    ): Call<String>

    @FormUrlEncoded
    @POST("FindPw.php")
    fun findPw(
        @Field("userId") userId: String
    ): Call<String>

    @FormUrlEncoded
    @POST("FindPw1.php")
    fun findPw1(
        @Field("userId") userId: String,
        @Field("userName") userName:String,
        @Field("userPhone") userPhone:String
    ): Call<String>

    @FormUrlEncoded
    @POST("FindPw2.php")
    fun findPw2(
        @Field("userId") userId: String,
        @Field("userPw") userPw: String
    ): Call<String>

}