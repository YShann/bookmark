package com.personal.bookmark_app.data.repository

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.personal.bookmark_app.ui.model.BookmarkSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.concurrent.TimeUnit

data class BookmarkMetadata(val url: String, val title: String, val content: String, val imageUrl: String?, val source: BookmarkSource)

internal class BookmarkBrowserChallengeException : IllegalStateException(
    "網站要求通過瀏覽器驗證，暫時無法自動取得標題和內文"
)

internal class BookmarkHttpException(val status: Int) : IllegalStateException("網站暫時無法讀取（$status）")

internal class BookmarkPreviewUnavailableException(message: String) : IllegalStateException(message)

class BookmarkMetadataParser internal constructor(private val client: OkHttpClient) {
    // Use a separate client so the API bearer token is never sent to third-party sites.
    constructor() : this(OkHttpClient.Builder().callTimeout(20, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS).followRedirects(true).build()
    )

    suspend fun parse(url: String): BookmarkMetadata = withContext(Dispatchers.IO) {
        require(url.toHttpUrlOrNull() != null) { "網址格式不正確" }
        val request = Request.Builder().url(url)
            .header("User-Agent", "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 Chrome/131.0 Mobile Safari/537.36")
            .header("Accept", "text/html,application/xhtml+xml")
            .build()
        client.newCall(request).execute().use { response ->
            if (response.header("cf-mitigated").equals("challenge", ignoreCase = true)) {
                throw BookmarkBrowserChallengeException()
            }
            if (!response.isSuccessful) throw BookmarkHttpException(response.code)
            val body = checkNotNull(response.body)
            check(body.contentType()?.subtype.orEmpty().contains("html")) { "此網址未提供可解析的網頁" }
            val finalUrl = response.request.url.toString()
            // Metadata near the start of the page is still usable when the rest exceeds the limit.
            val doc = response.peekBody(2L * 1024 * 1024).byteStream().use {
                Jsoup.parse(it, body.contentType()?.charset()?.name(), finalUrl)
            }
            metadataFromDocument(doc, finalUrl, url)
        }
    }
}

internal fun metadataFromDocument(doc: Document, finalUrl: String, originalUrl: String): BookmarkMetadata {
    if (Regex("/(accounts/)?(login|signin)(/|[?]|$)", RegexOption.IGNORE_CASE).containsMatchIn(finalUrl)) {
        throw BookmarkPreviewUnavailableException("此貼文需要登入，無法取得公開預覽")
    }
    fun meta(vararg names: String): String = names.firstNotNullOfOrNull { name ->
        doc.select("meta[property='$name'], meta[name='$name'], meta[itemprop='$name']")
            .firstNotNullOfOrNull { it.attr("content").trim().takeIf(String::isNotEmpty) }
    }.orEmpty()
    val structured = doc.structuredPost()
    val title = sequenceOf(
        meta("og:title"),
        meta("twitter:title"),
        meta("headline", "name"),
        structured?.text("headline", "name").orEmpty(),
        doc.selectFirst("article h1, main h1, h1")?.text().orEmpty(),
        doc.title()
    ).map(String::trim).firstOrNull { it.isNotEmpty() && it.lowercase() !in genericTitles }.orEmpty()
    val content = structured?.text("articleBody", "text").orEmpty()
        .ifBlank { doc.select("article p").take(4).joinToString(" ") { it.text() } }
        .ifBlank { structured?.text("description").orEmpty() }
        .ifBlank { meta("og:description", "twitter:description", "description") }
        .ifBlank { doc.select("main p").take(4).joinToString(" ") { it.text() } }
        .replace(Regex("\\s+"), " ").trim().take(500)
    if (title.isBlank() && content.isBlank()) {
        val message = if (doc.title().trim().lowercase() in genericTitles) {
            "網站未提供這篇貼文的公開預覽"
        } else {
            "無法取得貼文資料"
        }
        throw BookmarkPreviewUnavailableException(message)
    }
    val imageValue = meta("og:image", "twitter:image")
    val image = imageValue.takeIf { it.isNotBlank() }?.let { finalUrl.toHttpUrlOrNull()?.resolve(it)?.toString() }
        ?.takeIf { it.length <= 2048 }
    return BookmarkMetadata(finalUrl, title.ifBlank { content }.take(255), content, image,
        detectSource(finalUrl).takeUnless { it == BookmarkSource.WEB } ?: detectSource(originalUrl))
}

private val genericTitles = setOf("instagram", "threads", "youtube", "dcard", "login", "log in")
private val postTypes = setOf(
    "Article", "NewsArticle", "BlogPosting", "SocialMediaPosting", "DiscussionForumPosting", "VideoObject"
)

private fun Document.structuredPost(): JsonObject? = select("script[type=application/ld+json]")
    .asSequence()
    .mapNotNull {
        try {
            JsonParser.parseString(it.data())
        } catch (_: JsonParseException) {
            null
        }
    }
    .flatMap { it.postCandidates() }
    .firstOrNull { candidate ->
        val type = candidate.get("@type")
        val types = if (type?.isJsonArray == true) type.asJsonArray else listOfNotNull(type)
        types.any {
            it.isJsonPrimitive && it.asJsonPrimitive.isString && it.asString.substringAfterLast('/') in postTypes
        } && candidate.text("headline", "name", "articleBody", "text", "description") != null
    }

private fun JsonElement.postCandidates(): Sequence<JsonObject> = when {
    isJsonArray -> asJsonArray.asSequence().flatMap { it.postCandidates() }
    isJsonObject -> sequenceOf("mainEntity", "@graph")
        .mapNotNull { asJsonObject.get(it) }
        .flatMap { it.postCandidates() } + sequenceOf(asJsonObject)
    else -> emptySequence()
}

private fun JsonObject.text(vararg keys: String): String? = keys.firstNotNullOfOrNull { key ->
    get(key)?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isString }
        ?.asString?.trim()?.takeIf(String::isNotEmpty)
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
