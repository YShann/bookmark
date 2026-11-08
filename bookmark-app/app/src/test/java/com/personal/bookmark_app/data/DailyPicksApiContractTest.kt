package com.personal.bookmark_app.data

import com.google.gson.Gson
import com.personal.bookmark_app.data.remote.api.DailyPicksApi
import com.personal.bookmark_app.data.remote.dto.response.bookmark.DailyPicksResponse
import com.personal.bookmark_app.data.remote.dto.request.bookmark.CreateBookmarkRequest
import org.junit.Assert.*
import org.junit.Test
import retrofit2.http.GET
import retrofit2.http.POST

class DailyPicksApiContractTest {
    private val gson = Gson()
    @Test fun newEndpointsUseGetAndPostWithoutClientSelectionParameters() {
        val get = DailyPicksApi::class.java.methods.single { it.name == "getDailyPicks" }
        val refresh = DailyPicksApi::class.java.methods.single { it.name == "refreshDailyPicks" }
        assertEquals("api/daily-picks", get.getAnnotation(GET::class.java).value)
        assertEquals("api/daily-picks/refresh", refresh.getAnnotation(POST::class.java).value)
        assertEquals(1, get.parameterCount) // coroutine continuation only
        assertEquals(1, refresh.parameterCount)
    }
    @Test fun insufficientResponseHasNoCards() {
        val response = gson.fromJson("""{"selectionDate":"2026-11-09","bookmarkCount":2,"isSufficient":false,"dailyPicks":[]}""", DailyPicksResponse::class.java)
        assertFalse(response.isSufficient)
        assertEquals(2L, response.bookmarkCount)
        assertTrue(response.orderedBookmarks().isEmpty())
    }
    @Test fun nestedBookmarksAreOrderedAndKeepDisplayFields() {
        fun item(position: Int) = """{"position":$position,"bookmark":{"id":$position,"folderId":5,"url":"https://example.org/$position","title":"標題","content":"摘要","note":"筆記","source":"THREADS","imageUrl":null,"tags":["旅行"],"createdAt":"2026-11-08T12:00:00","updatedAt":null}}"""
        val response = gson.fromJson("""{"selectionDate":"2026-11-09","bookmarkCount":3,"isSufficient":true,"dailyPicks":[${item(3)},${item(1)},${item(2)}]}""", DailyPicksResponse::class.java)
        assertTrue(response.isSufficient)
        assertEquals(listOf(1L,2L,3L), response.orderedBookmarks().map { it.id })
        assertEquals(listOf("旅行"), response.orderedBookmarks().first().tags)
        assertEquals("摘要", response.orderedBookmarks().first().content)
    }
    @Test fun createRequestCarriesDailyPickChoiceAndMetadata() {
        val request=CreateBookmarkRequest("https://example.org", "title", null, "THREADS", emptyList(), "summary", null, true)
        val json=gson.toJsonTree(request).asJsonObject
        assertTrue(json.get("addToDailyPicks").asBoolean)
        assertEquals("THREADS", json.get("source").asString)
        assertEquals("summary", json.get("content").asString)
    }
}
