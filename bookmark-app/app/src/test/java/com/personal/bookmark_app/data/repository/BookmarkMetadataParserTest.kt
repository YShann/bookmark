package com.personal.bookmark_app.data.repository

import com.personal.bookmark_app.ui.model.BookmarkSource
import org.jsoup.Jsoup
import org.junit.Assert.*
import org.junit.Test

class BookmarkMetadataParserTest {
    @Test fun extractsUrlFromSharedTextAndTrimsClosingPunctuation() {
        assertEquals("https://www.threads.net/@user/post/123?x=1", extractSharedUrl("看看這篇（https://www.threads.net/@user/post/123?x=1）"))
        assertNull(extractSharedUrl("沒有網址"))
        assertNull(extractSharedUrl("file:///private/data"))
    }
    @Test fun detectsPlatformWithoutMatchingImpersonatingHosts() {
        assertEquals(BookmarkSource.THREADS, detectSource("https://www.threads.com/@a/post/b"))
        assertEquals(BookmarkSource.INSTAGRAM, detectSource("https://instagram.com/p/abc"))
        assertEquals(BookmarkSource.DCARD, detectSource("https://www.dcard.tw/f/travel/p/123"))
        assertEquals(BookmarkSource.YOUTUBE, detectSource("https://youtu.be/abc"))
        assertEquals(BookmarkSource.WEB, detectSource("https://instagram.com.example.org/p/abc"))
    }
    @Test fun parsesOpenGraphDecodesEntitiesAndLimitsSummary() {
        val description = "摘要".repeat(400)
        val doc = Jsoup.parse("""<html><head><title>備用標題</title>
            <meta property="og:title" content="貼文 &amp; 分享">
            <meta name="description" content="$description">
            <meta property="og:image" content="/cover.jpg"></head></html>""")
        val result = metadataFromDocument(doc, "https://www.dcard.tw/f/travel/p/123", "https://www.dcard.tw/f/travel/p/123")
        assertEquals("貼文 & 分享", result.title)
        assertEquals(500, result.content.length)
        assertEquals("https://www.dcard.tw/cover.jpg", result.imageUrl)
        assertEquals(BookmarkSource.DCARD, result.source)
    }
    @Test fun fallsBackToArticleParagraphsWithoutSavingNavigationText() {
        val doc = Jsoup.parse("<title>文章</title><nav>不該儲存</nav><article><p>第一段</p><p>第二段</p></article>")
        val result = metadataFromDocument(doc, "https://example.org/post", "https://example.org/post")
        assertEquals("第一段 第二段", result.content)
        assertNull(result.imageUrl)
    }
    @Test(expected = IllegalStateException::class) fun loginRedirectIsNotSavedAsPostMetadata() {
        metadataFromDocument(Jsoup.parse("<title>登入</title>"), "https://www.instagram.com/accounts/login/", "https://www.instagram.com/p/abc")
    }
}
