package com.example.test30

import retrofit2.Call
import retrofit2.http.*


interface RegisterInterface {
    @FormUrlEncoded
    @POST("Register.php")
    fun getUserRegist(
        @Field("userId") userId: String,
        @Field("userPw") userPw: String,
        @Field("userName") userName: String,
        @Field("userGender") userGender: String,
        @Field("userBirth") userBirth: String,
        @Field("userPhone") userPhone: String,
        @Field("userTelephone") userTelephone: String,
        @Field("userGuardianphone") userGuardianphone: String,
        @Field("userAddress") userAddress: String,
        @Field("userCrtd") userCrtd: String,
        @Field("userCrtu") userCrtu: String
    ): Call<String>

    @GET("CheckId.php")
    fun checkIdDup(
        @Query("userId") userId: String
    ):Call<String>

    @GET("CheckPhone.php")
    fun checkPhoneDup(
        @Query("userPhone") userPhone: String
    ):Call<String>

    @FormUrlEncoded
    @POST("DeleteUser.php")
    fun deleteUser(
        @Field("userId") userId: String
    ): Call<String>

    companion object {
        const val REGIST_URL = "http://sejongcountry.dothome.co.kr/"
    }

//    //GET 예제
//    @GET("posts/1")
//    fun getUser(): Call<User>
//
//    @GET("posts/{page}")
//    fun getUserPage(@Path("page") page: String): Call<User>


//    @GET("posts/1")
//    fun getStudent(@Query("school_id") schoolId: Int,
//                   @Query("grade") grade: Int,
//                   @Query("classroom") classroom: Int): Call<ExampleResponse>
//
//
//    //POST 예제
//    @FormUrlEncoded
//    @POST("posts")
//    fun getContactsObject(@Field("idx") idx: String): Call<JsonObject>

}