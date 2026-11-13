package com.personal.bookmark_app.data.repository

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

private val dcardPostPath = Regex("^/(?:f/[^/]+/p|@[^/]+/post)/([0-9]+)/?$")
private val threadsPostPath = Regex("^/(?:@[^/]+/post|t)/([A-Za-z0-9_-]+)/?$")
private val instagramPostPath = Regex("^/(?:[^/]+/)?(?:p|reel|tv)/([A-Za-z0-9_-]+)/?$")
private val sharePath = Regex("^/share/(?:reel/|p/)?([A-Za-z0-9_-]+)/?$")
private val whitespace = Regex("\\s+")

internal enum class BrowserPostSite {
    DCARD, THREADS, INSTAGRAM
}

internal data class BrowserPostLink(
    val site: BrowserPostSite,
    val id: String,
    val isShare: Boolean = false
)

internal fun browserPostSite(value: String): BrowserPostSite? {
    val url = value.toHttpUrlOrNull() ?: return null
    if (!url.isHttps || url.port != 443 || url.username.isNotEmpty() || url.password.isNotEmpty()) return null
    return when (url.host.removePrefix("www.")) {
        "dcard.tw" -> BrowserPostSite.DCARD
        "threads.com", "threads.net" -> BrowserPostSite.THREADS
        "instagram.com" -> BrowserPostSite.INSTAGRAM
        else -> null
    }
}

internal fun browserPostLink(value: String): BrowserPostLink? {
    val site = browserPostSite(value) ?: return null
    val path = value.toHttpUrlOrNull()?.encodedPath ?: return null
    val postPath = when (site) {
        BrowserPostSite.DCARD -> dcardPostPath
        BrowserPostSite.THREADS -> threadsPostPath
        BrowserPostSite.INSTAGRAM -> instagramPostPath
    }
    postPath.matchEntire(path)?.let { return BrowserPostLink(site, it.groupValues[1]) }
    return sharePath.matchEntire(path)?.takeIf { site != BrowserPostSite.DCARD }
        ?.let { BrowserPostLink(site, it.groupValues[1], isShare = true) }
}

internal fun browserPostMetadata(
    requestedUrl: String,
    pageUrl: String,
    postUrl: String,
    headline: String,
    body: String,
    image: String?
): BookmarkMetadata? {
    val requested = browserPostLink(requestedUrl) ?: return null
    val page = browserPostLink(pageUrl) ?: return null
    val post = browserPostLink(postUrl) ?: return null
    if (post.isShare || post.site != requested.site) return null
    if (requested.isShare) {
        if (page != requested && page != post) return null
    } else if (page != requested || post != requested) {
        return null
    }
    val title = headline.trim().take(255)
    val content = body.replace(whitespace, " ").trim().take(500)
    if (title.isBlank() || content.isBlank()) return null
    val imageUrl = image?.toHttpUrlOrNull()?.takeIf { it.isHttps }
        ?.toString()?.takeIf { it.length <= 2048 }
    return BookmarkMetadata(requestedUrl, title, content, imageUrl)
}
