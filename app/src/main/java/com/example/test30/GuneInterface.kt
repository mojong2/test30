package com.example.test30

import retrofit2.Call
import retrofit2.http.*

interface GuneInterface {

    @FormUrlEncoded
    @POST("InsertGune.php")
    fun insertGune(
        @Field("boardTitle") boardTitle: String,
        @Field("boardContent") boardContent: String,
        @Field("boardType") boardType: String,
        @Field("boardStatus") boardStatus: String,
        @Field("userId") userId: String,
        @Field("boardCrtu") boardCrtu: String,
        @Field("boardCrtd") boardCrtd: String
    ): Call<String>

    @GET("SelectGuneUserList.php")
    fun selectGuneUserList(
        @Query("userId") userId: String
    ): Call<String>

    @GET("SelectGuneAdminList.php")
    fun selectGuneAdminList(

    ): Call<String>

    @FormUrlEncoded
    @POST("SelectGuneNo.php")
    fun selectGuneNo(
        @Field("boardNo") boardNo: Int
    ): Call<String>

    @FormUrlEncoded
    @POST("SelectGuneReplyNo.php")
    fun selectGuneReplyNo(
        @Field("boardNo") boardNo: Int
    ): Call<String>

    @FormUrlEncoded
    @POST("DeleteGune.php")
    fun deleteGune(
        @Field("boardNo") boardNo: Int
    ): Call<String>

    @FormUrlEncoded
    @POST("SelectGuneReplyList.php")
    fun selectGuneReplyList(
        @Field("boardNo") boardNo: Int
    ): Call<String>

    @FormUrlEncoded
    @POST("InsertReplyGune.php")
    fun insertReplyGune(
        @Field("replyBoardNo") replyBoardNo: Int,
        @Field("replyContent") replyContent: String,
        @Field("replyUserId") replyUserId: String,
        @Field("replyCrtd") replyCrtd: String,
        @Field("replyCrtu") replyCrtu: String
    ): Call<String>

    @FormUrlEncoded
    @POST("DeleteReplyGune.php")
    fun deleteReplyGune(
        @Field("replyNo") replyNo: Int
    ): Call<String>

}