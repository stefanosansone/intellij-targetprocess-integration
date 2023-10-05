package com.github.stefanosansone.intellijtargetprocessintegration.api

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class TargetProcessService {
    private val client = OkHttpClient()

    fun fetchDataFromUrl(url: String): String? {
        val request = Request.Builder()
            .url(url)
            .build()

        return try {
            val response: Response = client.newCall(request).execute()

            if (response.isSuccessful) {
                response.body?.string()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}