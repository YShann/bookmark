package com.personal.bookmark_app.data.repository

import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.jsoup.Jsoup
import org.junit.Assert.*
import org.junit.Test

class BookmarkMetadataParserTest {
    @Test fun extractsUrlFromSharedTextAndTrimsClosingPunctuation() {
        assertEquals("https://www.threads.net/@user/post/123?x=1", extractSharedUrl("看看這篇（https://www.threads.net/@user/post/123?x=1）"))
        assertNull(extractSharedUrl("沒有網址"))
        assertNull(extractSharedUrl("file:///private/data"))
    }
    @Test fun parsesOpenGraphDecodesEntitiesAndLimitsSummary() {
        val description = "摘要".repeat(400)
        val doc = Jsoup.parse("""<html><head><title>備用標題</title>
            <meta property="og:title" content="貼文 &amp; 分享">
            <meta name="description" content="$description">
            <meta property="og:image" content="/cover.jpg"></head></html>""")
        val result = metadataFromDocument(doc, "https://www.dcard.tw/f/travel/p/123")
        assertEquals("貼文 & 分享", result.title)
        assertEquals(500, result.content.length)
        assertEquals("https://www.dcard.tw/cover.jpg", result.imageUrl)
    }
    @Test fun fallsBackToArticleParagraphsWithoutSavingNavigationText() {
        val doc = Jsoup.parse("<title>文章</title><nav>不該儲存</nav><article><p>第一段</p><p>第二段</p></article>")
        val result = metadataFromDocument(doc, "https://example.org/post")
        assertEquals("第一段 第二段", result.content)
        assertNull(result.imageUrl)
    }

    @Test fun parsesMetadataBeforeLargePageBody() = runBlocking {
        val html = """
            <meta property="og:title" content="文章標題">
            <meta property="og:description" content="文章摘要">
        """.trimIndent() + "<body>" + "x".repeat(2 * 1024 * 1024) + "</body>"
        val result = BookmarkMetadataParser(htmlClient(html)).parse("https://example.org/post")
        assertEquals("文章標題", result.title)
        assertEquals("文章摘要", result.content)
    }

    @Test fun parsesStructuredArticleInsteadOfGenericPageTitle() {
        val doc = Jsoup.parse("""
            <title>Threads</title>
            <meta property="og:title" content="Threads">
            <meta name="description" content="平台介紹">
            <script type="application/ld+json">{ invalid</script>
            <script type="application/ld+json">
            {"@graph":[
              {"@type":"WebSite","name":"Threads"},
              {"@type":["Article","SocialMediaPosting"],"headline":"貼文標題","articleBody":"貼文第一段\n貼文第二段"}
            ]}
            </script>
        """.trimIndent())
        val url = "https://www.threads.com/@user/post/123"
        val result = metadataFromDocument(doc, url)

        assertEquals("貼文標題", result.title)
        assertEquals("貼文第一段 貼文第二段", result.content)
    }

    @Test fun readsVideoMicrodataWhenOpenGraphTagsAreEmpty() {
        val doc = Jsoup.parse("""
            <meta property="og:title" content="">
            <meta itemprop="name" content="影片標題">
            <meta itemprop="description" content="影片介紹">
        """.trimIndent())
        val url = "https://www.youtube.com/watch?v=example"
        val result = metadataFromDocument(doc, url)

        assertEquals("影片標題", result.title)
        assertEquals("影片介紹", result.content)
    }

    @Test fun reportsBrowserChallengeInsteadOfReadingItsTitle() {
        val client = htmlClient("<title>Just a moment...</title>", browserChallenge = true)
        assertThrows(BookmarkBrowserChallengeException::class.java) {
            runBlocking { BookmarkMetadataParser(client).parse("https://www.dcard.tw/f/relationship/p/123") }
        }
    }

    @Test(expected = IllegalStateException::class) fun loginRedirectIsNotSavedAsPostMetadata() {
        metadataFromDocument(Jsoup.parse("<title>登入</title>"), "https://www.instagram.com/accounts/login/")
    }

    private fun htmlClient(html: String, browserChallenge: Boolean = false): OkHttpClient =
        OkHttpClient.Builder().addInterceptor { chain ->
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(if (browserChallenge) 403 else 200)
                .message(if (browserChallenge) "Forbidden" else "OK")
                .apply { if (browserChallenge) header("cf-mitigated", "challenge") }
                .body(html.toResponseBody("text/html; charset=utf-8".toMediaType()))
                .build()
        }.build()
}
