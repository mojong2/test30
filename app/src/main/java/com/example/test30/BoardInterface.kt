package com.example.test30

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface BoardInterface {

    @FormUrlEncoded
    @POST("InsertBoard.php")
    fun insertBoard(
        @Field("boardTitle") boardTitle: String,
        @Field("boardContent") boardContent: String,
        @Field("boardType") boardType: String,
        @Field("boardStatus") boardStatus: String,
        @Field("userId") userId: String,
        @Field("boardCrtu") boardCrtu: String,
        @Field("boardCrtd") boardCrtd: String
    ): Call<String>

    @GET("SelectBoardListImp.php")
    fun selectBoardListImp(

    ): Call<String>

    @FormUrlEncoded
    @POST("SelectBoardListNor.php")
    fun selectBoardListNor(
        @Field("DAY") DAY: String
    ): Call<String>

    @FormUrlEncoded
    @POST("SelectBoard.php")
    fun selectBoard(
        @Field("boardNo") boardNo: Int
    ): Call<String>

    @FormUrlEncoded
    @POST("SelectReplyBoardNo.php")
    fun selectReplyNo(
        @Field("boardNo") boardNo: Int
    ): Call<String>

    @FormUrlEncoded
    @POST("DeleteBoard.php")
    fun deleteBoard(
        @Field("boardNo") boardNo: Int
    ): Call<String>

    @FormUrlEncoded
    @POST("UpdateBoard.php")
    fun updateBoard(
        @Field("boardNo") boardNo: Int,
        @Field("boardTitle") boardTitle: String,
        @Field("boardContent") boardContent: String,
        @Field("boardType") boardType: String,
        @Field("boardArtu") boardCrtu: String,
        @Field("boardArtd") boardCrtd: String
    ): Call<String>

    @FormUrlEncoded
    @POST("SelectBoardReplyList.php")
    fun selectBoardReplyList(
        @Field("boardNo") boardNo: Int
    ): Call<String>

    @FormUrlEncoded
    @POST("InsertReplyBoard.php")
    fun insertReplyBoard(
        @Field("replyBoardNo") replyBoardNo: Int,
        @Field("replyContent") replyContent: String,
        @Field("replyUserId") replyUserId: String,
        @Field("replyCrtd") replyCrtd: String,
        @Field("replyCrtu") replyCrtu: String
    ): Call<String>

    @FormUrlEncoded
    @POST("DeleteReplyBoard.php")
    fun deleteReplyBoard(
        @Field("replyNo") replyNo: Int
    ): Call<String>

}