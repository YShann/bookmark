package com.personal.bookmark_app.data.repository

import com.personal.bookmark_app.ui.model.BookmarkSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.jsoup.Jsoup
import java.util.concurrent.TimeUnit

data class BookmarkMetadata(val url: String, val title: String, val content: String, val imageUrl: String?, val source: BookmarkSource)

class BookmarkMetadataParser {
    // Never send the authenticated API client's bearer token to third-party sites.
    private val client = OkHttpClient.Builder().callTimeout(20, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS).followRedirects(true).build()

    suspend fun parse(url: String): BookmarkMetadata = withContext(Dispatchers.IO) {
        require(url.toHttpUrlOrNull() != null) { "網址格式不正確" }
        client.newCall(Request.Builder().url(url)
            .header("User-Agent", "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 Chrome/131.0 Mobile Safari/537.36")
            .header("Accept", "text/html,application/xhtml+xml").build()).execute().use { response ->
            check(response.isSuccessful) { "網站暫時無法讀取（${response.code}）" }
            val body = checkNotNull(response.body)
            check(body.contentType()?.subtype.orEmpty().contains("html")) { "此網址未提供可解析的網頁" }
            val bytes = body.byteStream().readBytesLimited(2 * 1024 * 1024)
            val finalUrl = response.request.url.toString()
            val doc = Jsoup.parse(bytes.inputStream(), body.contentType()?.charset()?.name(), finalUrl)
            metadataFromDocument(doc, finalUrl, url)
        }
    }
}

internal fun metadataFromDocument(doc: org.jsoup.nodes.Document, finalUrl: String, originalUrl: String): BookmarkMetadata {
    check(!Regex("/(accounts/)?(login|signin)(/|[?]|$)", RegexOption.IGNORE_CASE).containsMatchIn(finalUrl)) {
        "此貼文需要登入，無法取得公開預覽"
    }
    fun meta(vararg names: String): String = names.firstNotNullOfOrNull { name ->
        doc.selectFirst("meta[property='$name'], meta[name='$name']")?.attr("content")?.trim()?.takeIf { it.isNotEmpty() }
    }.orEmpty()
    val title = meta("og:title", "twitter:title").ifBlank { doc.title() }.trim().take(255)
    val content = meta("og:description", "twitter:description", "description").ifBlank {
        doc.select("article p, main p").take(4).joinToString(" ") { it.text() }
    }.replace(Regex("\\s+"), " ").trim().take(500)
    check(!(content.isBlank() && title.lowercase() in setOf("instagram", "threads", "youtube", "dcard", "login", "log in"))) {
        "網站未提供這篇貼文的公開預覽"
    }
    check(title.isNotBlank() || content.isNotBlank()) { "無法取得貼文資料，可重新解析或手動輸入標題" }
    val imageValue = meta("og:image", "twitter:image")
    val image = imageValue.takeIf { it.isNotBlank() }?.let { finalUrl.toHttpUrlOrNull()?.resolve(it)?.toString() }
        ?.takeIf { it.length <= 2048 }
    return BookmarkMetadata(finalUrl, title.ifBlank { content.take(255) }, content, image,
        detectSource(finalUrl).takeUnless { it == BookmarkSource.WEB } ?: detectSource(originalUrl))
}

private fun java.io.InputStream.readBytesLimited(limit: Int): ByteArray {
    val output = java.io.ByteArrayOutputStream()
    val buffer = ByteArray(8192)
    while (true) {
        val count = read(buffer)
        if (count < 0) break
        check(output.size() + count <= limit) { "網頁內容過大，請手動輸入標題" }
        output.write(buffer, 0, count)
    }
    return output.toByteArray()
}

fun extractSharedUrl(text: String): String? = Regex("https?://[^\\s<>\"\u3000]+", RegexOption.IGNORE_CASE)
    .findAll(text).map { it.value.trimEnd('.', ',', ';', '!', ')', ']', '}', '。', '，', '）', '】') }
    .firstOrNull { it.toHttpUrlOrNull() != null }

fun detectSource(url: String): BookmarkSource {
    val host = url.toHttpUrlOrNull()?.host.orEmpty().lowercase()
    fun domain(name: String) = host == name || host.endsWith(".$name")
    return when {
        domain("threads.net") || domain("threads.com") -> BookmarkSource.THREADS
        domain("instagram.com") -> BookmarkSource.INSTAGRAM
        domain("dcard.tw") -> BookmarkSource.DCARD
        domain("youtube.com") || domain("youtu.be") -> BookmarkSource.YOUTUBE
        else -> BookmarkSource.WEB
    }
}
