package com.personal.bookmark_app.data.remote

import com.google.gson.Gson
import com.personal.bookmark_app.data.remote.dto.response.ApiErrorResponse
import retrofit2.HttpException

internal fun HttpException.backendMessage(): String? = runCatching {
    response()?.errorBody()?.string()
        ?.let { Gson().fromJson(it, ApiErrorResponse::class.java)?.message }
        ?.takeIf(String::isNotBlank)
}.getOrNull()
