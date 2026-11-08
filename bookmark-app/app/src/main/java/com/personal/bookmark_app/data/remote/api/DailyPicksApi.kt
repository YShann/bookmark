package com.personal.bookmark_app.data.remote.api

import com.personal.bookmark_app.data.remote.dto.response.bookmark.DailyPicksResponse
import retrofit2.http.GET
import retrofit2.http.POST

interface DailyPicksApi {
    @GET("api/daily-picks")
    suspend fun getDailyPicks(): DailyPicksResponse

    @POST("api/daily-picks/refresh")
    suspend fun refreshDailyPicks(): DailyPicksResponse
}
