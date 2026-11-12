package com.personal.bookmark_app.ui.screen.addbookmark

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.personal.bookmark_app.data.repository.BookmarkMetadata
import com.personal.bookmark_app.data.repository.BrowserPostLink
import com.personal.bookmark_app.data.repository.BrowserPostSite
import com.personal.bookmark_app.data.repository.browserPostLink
import com.personal.bookmark_app.data.repository.browserPostMetadata
import com.personal.bookmark_app.data.repository.browserPostSite
import kotlinx.coroutines.delay
import org.json.JSONException
import org.json.JSONObject

private const val EXTRA_URL = "url"
private const val EXTRA_TITLE = "title"
private const val EXTRA_CONTENT = "content"
private const val EXTRA_IMAGE_URL = "imageUrl"

internal class BookmarkBrowserImportContract : ActivityResultContract<String, BookmarkMetadata?>() {
    override fun createIntent(context: Context, input: String): Intent =
        Intent(context, BookmarkBrowserImportActivity::class.java).putExtra(EXTRA_URL, input)

    override fun parseResult(resultCode: Int, intent: Intent?): BookmarkMetadata? {
        if (resultCode != Activity.RESULT_OK || intent == null) return null
        val url = intent.getStringExtra(EXTRA_URL) ?: return null
        return BookmarkMetadata(
            url = url,
            title = intent.getStringExtra(EXTRA_TITLE) ?: return null,
            content = intent.getStringExtra(EXTRA_CONTENT) ?: return null,
            imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL)
        )
    }
}

class BookmarkBrowserImportActivity : ComponentActivity() {
    private lateinit var webView: BookmarkImportWebView
    private lateinit var layout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val requestedUrl = intent.getStringExtra(EXTRA_URL).orEmpty()
        val post = browserPostLink(requestedUrl)
        if (post == null) {
            finish()
            return
        }
        webView = BookmarkImportWebView(this, requestedUrl, post, interactive = true) { metadata ->
            setResult(Activity.RESULT_OK, Intent()
                .putExtra(EXTRA_URL, metadata.url)
                .putExtra(EXTRA_TITLE, metadata.title)
                .putExtra(EXTRA_CONTENT, metadata.content)
                .putExtra(EXTRA_IMAGE_URL, metadata.imageUrl))
            finish()
        }
        layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(webView, LinearLayout.LayoutParams(-1, 0, 1f))
            addView(Button(context).apply {
                text = "匯入這篇文章"
                setOnClickListener {
                    webView.readPost {
                        Toast.makeText(
                            context, "請等原貼文的標題和內文顯示後再匯入", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }, LinearLayout.LayoutParams(-1, -2))
        }
        ViewCompat.setOnApplyWindowInsetsListener(layout) { view, insets ->
            val area = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            view.setPadding(area.left, area.top, area.right, area.bottom)
            insets
        }
        setContentView(layout)
        webView.loadUrl(requestedUrl)
    }

    override fun onDestroy() {
        if (::webView.isInitialized) {
            layout.removeView(webView)
            webView.destroy()
        }
        super.onDestroy()
    }
}

@Composable
internal fun BackgroundBookmarkImport(
    url: String,
    modifier: Modifier,
    onResult: (BookmarkMetadata?) -> Unit
) {
    val post = browserPostLink(url) ?: return
    key(url) {
        val reportResult by rememberUpdatedState(onResult)
        LaunchedEffect(Unit) {
            delay(15_000)
            reportResult(null)
        }
        AndroidView(
            modifier = modifier,
            factory = { context ->
                FrameLayout(context).apply {
                    alpha = 0f
                    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
                    descendantFocusability = FrameLayout.FOCUS_BLOCK_DESCENDANTS
                    val browser = BookmarkImportWebView(context, url, post, interactive = false) {
                        reportResult(it)
                    }
                    addView(browser, FrameLayout.LayoutParams(-1, -1))
                    browser.loadUrl(url)
                }
            },
            onRelease = { container ->
                val browser = container.getChildAt(0) as BookmarkImportWebView
                container.removeView(browser)
                browser.destroy()
            }
        )
    }
}

private class BookmarkImportWebView(
    context: Context,
    private val requestedUrl: String,
    private val requestedPost: BrowserPostLink,
    private val interactive: Boolean,
    private val onImported: (BookmarkMetadata) -> Unit
) : WebView(context) {
    private var navigationVersion = 0
    private var finished = false

    init {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.allowFileAccess = false
        settings.allowContentAccess = false
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                if (!request.isForMainFrame) return false
                val url = request.url.toString()
                if (browserPostSite(url) != requestedPost.site) return true
                val post = browserPostLink(url) ?: return false
                return post != requestedPost && (!requestedPost.isShare || post.isShare || request.hasGesture())
            }

            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                navigationVersion++
            }

            override fun onPageFinished(view: WebView, url: String?) {
                if (url != null && view.url == url && canReadPost(url)) {
                    readPost(navigationVersion, retriesLeft = 10)
                }
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean =
        interactive && super.dispatchTouchEvent(event)

    fun readPost(onUnavailable: () -> Unit) {
        readPost(navigationVersion, retriesLeft = 0, onUnavailable = onUnavailable)
    }

    private fun canReadPost(url: String): Boolean {
        val page = browserPostLink(url) ?: return false
        return page == requestedPost ||
            (requestedPost.isShare && !page.isShare && page.site == requestedPost.site)
    }

    private fun readPost(version: Int, retriesLeft: Int, onUnavailable: (() -> Unit)? = null) {
        if (finished || version != navigationVersion) return
        if (url?.let(::canReadPost) != true) {
            onUnavailable?.invoke()
            return
        }
        val script = if (requestedPost.site == BrowserPostSite.DCARD) READ_DCARD_POST else READ_META_POST
        evaluateJavascript(script) { json ->
            if (finished || version != navigationVersion) return@evaluateJavascript
            val post = try {
                JSONObject(json)
            } catch (_: JSONException) {
                null
            }
            val metadata = post?.let {
                browserPostMetadata(
                    requestedUrl,
                    it.optString("pageUrl"),
                    it.optString("postUrl"),
                    it.optString("headline"),
                    it.optString("body"),
                    it.optString("image")
                )
            }
            when {
                metadata != null -> {
                    finished = true
                    onImported(metadata)
                }
                retriesLeft > 0 -> postDelayed({ readPost(version, retriesLeft - 1) }, 750)
                else -> onUnavailable?.invoke()
            }
        }
    }

    override fun destroy() {
        finished = true
        stopLoading()
        super.destroy()
    }
}

private val READ_DCARD_POST = """
    (() => {
        for (const script of document.querySelectorAll('script[type="application/ld+json"]')) {
            let post;
            try {
                post = JSON.parse(script.textContent);
            } catch {
                continue;
            }
            if (post?.['@type'] !== 'SocialMediaPosting' ||
                typeof post.url !== 'string' ||
                typeof post.headline !== 'string' ||
                typeof post.text !== 'string') continue;

            const image = Array.isArray(post.image) ? post.image[0] : post.image;
            return {
                pageUrl: location.href,
                postUrl: post.url,
                headline: post.headline.trim().slice(0, 255),
                body: post.text.replace(/\s+/g, ' ').trim().slice(0, 500),
                image: typeof image === 'string' ? image.slice(0, 2048) : ''
            };
        }
        return null;
    })()
""".trimIndent()

private val READ_META_POST = """
    (() => {
        const meta = (name) => document.querySelector('meta[property="' + name + '"]')?.content?.trim();
        const pageUrl = location.href;
        const postUrl = meta('og:url') || document.querySelector('link[rel="canonical"]')?.href;
        if (!postUrl) return null;

        const headline = meta('og:title');
        const body = meta('og:description');
        if (!headline || !body || /^(instagram|threads)$/i.test(headline)) return null;

        return {
            pageUrl,
            postUrl,
            headline: headline.slice(0, 255),
            body: body.slice(0, 500),
            image: meta('og:image')?.slice(0, 2048)
        };
    })()
""".trimIndent()
