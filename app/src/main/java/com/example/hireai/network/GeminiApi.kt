package com.example.hireai.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GeminiApi {

    @Headers("Content-Type: application/json")
    @POST("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=AIzaSyDvtub-rpuOqMtBXep5WZM_kHAXyU5R7hk")
    suspend fun generateContent(
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<ResponseBody>
}