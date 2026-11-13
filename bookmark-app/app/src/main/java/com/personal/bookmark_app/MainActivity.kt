package com.personal.bookmark_app

import java.util.UUID
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.personal.bookmark_app.navigation.AppNavigation
import com.personal.bookmark_app.ui.theme.BookmarkappTheme
import dagger.hilt.android.AndroidEntryPoint

data class SharedBookmark(val id: String, val text: String, val title: String)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var sharedBookmark by mutableStateOf<SharedBookmark?>(null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedBookmark = savedInstanceState?.getString("shareId")?.let {
            SharedBookmark(
                it,
                savedInstanceState.getString("shareText").orEmpty(),
                savedInstanceState.getString("shareTitle").orEmpty()
            )
        }
        if (savedInstanceState == null) receiveShare(intent)
        enableEdgeToEdge()
        setContent {
            BookmarkappTheme {
                AppNavigation(sharedBookmark = sharedBookmark, onShareHandled = { id ->
                    if (sharedBookmark?.id == id) sharedBookmark = null
                })
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        receiveShare(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        sharedBookmark?.let {
            outState.putString("shareId", it.id)
            outState.putString("shareText", it.text)
            outState.putString("shareTitle", it.title)
        }
        super.onSaveInstanceState(outState)
    }

    private fun receiveShare(intent: Intent) {
        if (intent.action != Intent.ACTION_SEND || intent.type?.startsWith("text/") != true) return
        val text = intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
            ?: intent.clipData?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.text?.toString()
                .orEmpty()
        sharedBookmark = SharedBookmark(
            UUID.randomUUID().toString(), text.take(20000),
            intent.getStringExtra(Intent.EXTRA_SUBJECT).orEmpty().take(255)
        )
    }
}
