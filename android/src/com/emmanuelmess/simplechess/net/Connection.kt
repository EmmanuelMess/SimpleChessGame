package com.emmanuelmess.simplechess.net

import android.content.Context
import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException


object Connection: Networking {
    private val JSON = "application/json; charset=utf-8".toMediaType()
    private lateinit var client: OkHttpClient

    fun init(context: Context) {
        val cookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context))
        client = OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build()

    }

    @Throws(IOException::class)
    override fun logIn(username: String, password: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        post(
                "https://lichess.org/login",
                "{username: $username, password: $password }"
        ).let { response ->
            if (!response.isSuccessful || response.body == null) onFailure()
            onSuccess()
        }
    }


    @Throws(IOException::class)
    private fun post(url: String, json: String): Response {
        val request: Request = Request.Builder()
                .url(url)
                .post(json.toRequestBody(JSON))
                .build()
        client.newCall(request).execute().use { response ->
            return response
        }
    }

    override fun undo() {
        TODO()
    }

    override fun draw() {
        TODO()
    }

    override fun surrender() {
        TODO()
    }
}