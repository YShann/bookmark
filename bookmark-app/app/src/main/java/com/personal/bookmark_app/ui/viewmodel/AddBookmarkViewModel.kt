package com.personal.bookmark_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.personal.bookmark_app.data.remote.RetrofitClient
import com.personal.bookmark_app.data.remote.api.*
import com.personal.bookmark_app.data.remote.dto.request.bookmark.CreateBookmarkRequest
import com.personal.bookmark_app.ui.model.*
import com.personal.bookmark_app.ui.screen.addbookmark.AddBookmarkUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.http.GET
import retrofit2.http.Path

interface AddBookmarkTagApi {
    @GET("api/folders/{folderId}/tags")
    suspend fun getTags(@Path("folderId") folderId: Long): List<AddBookmarkTagResponse>
}
data class AddBookmarkTagResponse(val id: Long, val name: String)

class AddBookmarkViewModel(application: Application, private val savedState: androidx.lifecycle.SavedStateHandle) : AndroidViewModel(application) {
    private val retrofit = RetrofitClient.authenticated(application)
    private val folderApi = retrofit.create(FolderApi::class.java)
    private val bookmarkApi = retrofit.create(BookmarkApi::class.java)
    private val tagApi = retrofit.create(AddBookmarkTagApi::class.java)
    private val parser = com.personal.bookmark_app.data.repository.BookmarkMetadataParser()
    private var parseJob: kotlinx.coroutines.Job? = null
    private var parseGeneration = 0
    private var debounceJob: kotlinx.coroutines.Job? = null
    private val _state = MutableStateFlow(savedState.get<android.os.Bundle>("draft")?.let {
        AddBookmarkUiState(sourceUrl = it.getString("url").orEmpty(), title = it.getString("title").orEmpty(),
            note = it.getString("note").orEmpty(), content = it.getString("content").orEmpty(),
            imageUrl = it.getString("image"), selectedSource = com.personal.bookmark_app.data.repository.detectSource(it.getString("url").orEmpty()),
            isAddedToDailyPicks = it.getBoolean("daily", true))
    } ?: AddBookmarkUiState())
    val state = _state.asStateFlow()
    init {
        loadFolders()
        if (_state.value.sourceUrl.isNotBlank() && _state.value.content.isBlank()) parseUrl()
    }
    fun edit(transform: (AddBookmarkUiState) -> AddBookmarkUiState) {
        if (!_state.value.isSaving && !_state.value.isSaved) {
            _state.value = transform(_state.value).copy(errorMessage = null)
            persistDraft()
        }
    }
    private fun persistDraft() {
        val form = _state.value
        savedState["draft"] = android.os.Bundle().apply {
            putString("url", form.sourceUrl); putString("title", form.title); putString("note", form.note)
            putString("content", form.content); putString("image", form.imageUrl); putBoolean("daily", form.isAddedToDailyPicks)
        }
    }

    fun receiveShare(share: com.personal.bookmark_app.SharedBookmark) {
        if (savedState.get<String>("shareId") == share.id) return
        savedState["shareId"] = share.id
        debounceJob?.cancel()
        parseJob?.cancel()
        parseGeneration++
        val url = com.personal.bookmark_app.data.repository.extractSharedUrl(share.text)
        _state.value = _state.value.copy(sourceUrl = url.orEmpty(), title = share.title,
            note = "", selectedTagIds = emptySet(), isParsing = false,
            content = "", imageUrl = null, isSaved = false, isSaving = false,
            selectedSource = com.personal.bookmark_app.data.repository.detectSource(url.orEmpty()),
            parseError = if (url == null) "分享內容沒有有效的 HTTP／HTTPS 網址，請手動輸入" else null)
        persistDraft()
        if (url != null) parseUrl()
    }

    fun changeUrl(value: String) {
        if (_state.value.isSaving) return
        debounceJob?.cancel()
        parseJob?.cancel()
        parseGeneration++
        val validUrl = value.isNotBlank() && com.personal.bookmark_app.data.repository.extractSharedUrl(value.trim()) == value.trim()
        edit { it.copy(sourceUrl = value, content = "", imageUrl = null, isParsing = validUrl, parseError = null,
            selectedSource = com.personal.bookmark_app.data.repository.detectSource(value.trim())) }
        if (validUrl) {
            debounceJob = viewModelScope.launch { kotlinx.coroutines.delay(700); parseUrl() }
        }
    }

    fun parseUrl() {
        debounceJob?.cancel()
        if (_state.value.isSaving) return
        val url = _state.value.sourceUrl.trim()
        if (com.personal.bookmark_app.data.repository.extractSharedUrl(url) != url || url.isBlank()) {
            _state.value = _state.value.copy(parseError = "請先輸入有效的 HTTP／HTTPS 網址")
            return
        }
        parseJob?.cancel()
        val generation = ++parseGeneration
        val originalTitle = _state.value.title
        _state.value = _state.value.copy(isParsing = true, parseError = null)
        parseJob = viewModelScope.launch {
            try {
                val result = parser.parse(url)
                if (generation == parseGeneration) {
                    _state.value = _state.value.copy(sourceUrl = result.url,
                        title = if (_state.value.title == originalTitle) result.title else _state.value.title,
                        content = result.content, imageUrl = result.imageUrl, selectedSource = result.source)
                    persistDraft()
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                if (generation == parseGeneration) _state.value = _state.value.copy(
                    parseError = (e.message ?: "解析失敗") + "，可重新解析或手動輸入標題後儲存")
            } finally {
                if (generation == parseGeneration) _state.value = _state.value.copy(isParsing = false)
            }
        }
    }

    private fun request(resetBusy: Boolean = true, action: suspend () -> Unit) {
        viewModelScope.launch {
            try { action() } catch (e: Exception) {
                if (e is CancellationException) throw e
                _state.value = _state.value.copy(isLoading = if (resetBusy) false else _state.value.isLoading, isSaving = if (resetBusy) false else _state.value.isSaving,
                    errorMessage = "操作失敗，請檢查網路後重試")
            }
        }
    }
    fun loadFolders() {
        if (_state.value.isSaving) return
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        request {
            val folders = folderApi.getFolders().map { FolderUiModel(it.id, it.name, bookmarkApi.getFolderBookmarks(it.id).size, it.imageUrl.orEmpty(), null) }
            _state.value = _state.value.copy(folders = folders, isLoading = false)
            selectFolder(folders.firstOrNull { it.title == "未分類" }?.id ?: folders.firstOrNull()?.id ?: 0L)
        }
    }
    fun selectFolder(id: Long) {
        if (_state.value.isSaving) return
        edit { it.copy(selectedFolderId = id) }
        if (id <= 0) return
        request(resetBusy = false) {
            val tags = tagApi.getTags(id).map { TagOptionUiModel(it.name, it.name) }
            if (_state.value.selectedFolderId == id)
                _state.value = _state.value.copy(tags = (tags + _state.value.tags.filter {
                    it.id in _state.value.selectedTagIds }).distinctBy { it.id })
        }
    }
    fun createFolder(name: String) {
        if (_state.value.isLoading || _state.value.isSaving) return
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        request {
            val f = folderApi.createFolder(FolderNameRequest(name.trim()))
            _state.value = _state.value.copy(isLoading = false,
                folders = _state.value.folders + FolderUiModel(f.id, f.name, 0, f.imageUrl.orEmpty(), null))
            selectFolder(f.id)
        }
    }
    fun toggleTag(id: String) = edit {
        it.copy(selectedTagIds = if (id in it.selectedTagIds) it.selectedTagIds - id
            else if (it.selectedTagIds.size < 5) it.selectedTagIds + id else it.selectedTagIds)
    }
    fun addTag(name: String) {
        val tag = name.trim()
        if (tag.isEmpty() || tag.length > 30 || _state.value.isTagLimitReached) return
        edit { it.copy(tags = (listOf(TagOptionUiModel(tag, tag)) + it.tags).distinctBy { it.id },
            selectedTagIds = it.selectedTagIds + tag) }
    }
    fun save() {
        val form = _state.value
        if (!form.isFormValid) return
        _state.value = form.copy(isSaving = true, errorMessage = null)
        request {
            bookmarkApi.createBookmark(form.selectedFolderId, CreateBookmarkRequest(
                url = form.sourceUrl.trim(), title = form.title.trim(), note = form.note.trim().ifBlank { null },
                source = form.selectedSource.name, tags = form.selectedTagIds.toList(),
                content = form.content.take(500).ifBlank { null }, imageUrl = form.imageUrl,
                addToDailyPicks = form.isAddedToDailyPicks))
            savedState.remove<android.os.Bundle>("draft")
            _state.value = _state.value.copy(isSaving = false, isSaved = true)
        }
    }
}
