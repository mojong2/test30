package com.example.test30

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface FcmInterface {
    @POST("fcm/send")
    suspend fun sendNotification(
        @Body notification: NotificationBody
    ) : Response<ResponseBody>

    @FormUrlEncoded
    @POST("SendPush.php")
    fun sendPush(
        @Field("TOKEN") TOKEN: String,
        @Field("TITLE") TITLE: String,
        @Field("MESSAGE") MESSAGE: String
    ): Call<String>
}