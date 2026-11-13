package com.personal.bookmark_app.data.remote

import androidx.paging.InvalidatingPagingSourceFactory
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.personal.bookmark_app.data.remote.dto.response.PageResponse
import kotlinx.coroutines.CancellationException

private const val PAGE_SIZE = 20

// 三個列表都以頁碼查 API。畫面滑到接近底部時，Paging 會呼叫 load。
class PageLoader<T : Any>(loadPage: suspend (page: Int, size: Int) -> PageResponse<T>) {
    private val sources = InvalidatingPagingSourceFactory {
        object : PagingSource<Int, T>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
                return try {
                    val page = params.key ?: 0
                    var response = loadPage(page, PAGE_SIZE)
                    // 刪除資料後，原本所在頁可能已經不存在。
                    if (params is LoadParams.Refresh && page > 0 && page >= response.totalPages) {
                        response = loadPage((response.totalPages - 1).coerceAtLeast(0), PAGE_SIZE)
                    }
                    LoadResult.Page(
                        data = response.content,
                        prevKey = (response.page - 1).takeIf { it >= 0 },
                        nextKey = (response.page + 1).takeIf { it < response.totalPages }
                    )
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    LoadResult.Error(error)
                }
            }

            override fun getRefreshKey(state: PagingState<Int, T>): Int? {
                val position = state.anchorPosition ?: return null
                val page = state.closestPageToPosition(position) ?: return null
                return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
            }
        }
    }

    val flow = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            initialLoadSize = PAGE_SIZE,
            prefetchDistance = 3,
            enablePlaceholders = false
        ),
        pagingSourceFactory = sources
    ).flow

    fun refresh() = sources.invalidate()
}
