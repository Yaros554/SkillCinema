package com.skyyaros.skillcinema.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.skyyaros.skillcinema.entity.ImageItem
import kotlinx.coroutines.delay
import kotlin.math.min

class PhotoPagingSource(
    private val kinopoiskRepositoryDefault: KinopoiskRepositoryDefault,
    private val id: Long,
    private val type: String,
    private val pageInit: Int,
    private val owner: String,
    private val curStack: String
): PagingSource<Int, ImageItem>() {
    override fun getRefreshKey(state: PagingState<Int, ImageItem>): Int {
        return pageInit
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageItem> {
        val page = params.key ?: pageInit
        val indexStart = (page - 1) * 18
        val indexEnd = page * 18 - 1
        val realPageStart = indexStart / 20 + 1
        val realPageEnd = indexEnd / 20 + 1
        val final = if (realPageStart == realPageEnd) {
            val res = kinopoiskRepositoryDefault.getImagesForFilmPaging(id, realPageStart, type)
            if (res != null) {
                if (res.items.isNotEmpty() && (indexStart % 20) in res.items.indices) {
                    res.items.subList(indexStart % 20, min(indexEnd % 20, res.items.size - 1) + 1)
                } else {
                    emptyList()
                }
            } else {
                null
            }
        } else {
            val res = kinopoiskRepositoryDefault.getImagesForFilmPaging(id, realPageStart, type)
            if (res != null) {
                if (res.items.isNotEmpty() && (indexStart % 20) in res.items.indices) {
                    val part1 = res.items.subList(indexStart % 20, res.items.size)
                    val res2 = kinopoiskRepositoryDefault.getImagesForFilmPaging(id, realPageEnd, type)
                    if (res2 != null) {
                        if (res2.items.isNotEmpty()) {
                            val part2 = res2.items.subList(0, min(indexEnd % 20, res2.items.size - 1) + 1)
                            part1 + part2
                        } else {
                            part1
                        }
                    } else {
                        null
                    }
                } else {
                    emptyList()
                }
            } else {
                null
            }
        }
        return if (final != null) {
            if (page != pageInit) {
                kinopoiskRepositoryDefault.getPermissionPreload(
                    curStack,
                    if (owner == "PhotographyFragment") type else null
                )
            }
            //Log.d("My_mutex", "Load page $page for $owner")
            LoadResult.Page(
                data = final,
                prevKey = if (page > 1) page - 1 else null,
                nextKey = if (final.isEmpty()) null else page + 1)
        } else {
            LoadResult.Error(Throwable("Error"))
        }
    }
}