package com.Yogify.birthdayreminder.ui

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.Yogify.birthdayreminder.db.DataDAO
import com.Yogify.birthdayreminder.model.ReminderItem
import kotlinx.coroutines.delay
import javax.inject.Inject

class MainPagingSource @Inject constructor(private val dao: DataDAO) :
    PagingSource<Int, ReminderItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ReminderItem> {
        val page = params.key ?: 0

        return try {
            var entities= dao.getReminder(params.loadSize, page * params.loadSize)
            // simulate page loading
            if (page != 0) delay(1000)
            LoadResult.Page(
                data = entities,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (entities.isEmpty()) null else page + 1
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ReminderItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}