package com.mulberry.ody.data.remote.thirdparty.address

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mulberry.ody.data.remote.thirdparty.address.response.toAddresses
import com.mulberry.ody.domain.apiresult.getOrThrow
import com.mulberry.ody.domain.model.Address

class AddressPagingSource(
    private val kakaoAddressService: KakaoAddressService,
    private val keyword: String,
    private val pageSize: Int,
) : PagingSource<Int, Address>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Address> {
        val page = params.key ?: PAGE_START
        return runCatching {
            val addresses =
                kakaoAddressService.fetchAddresses(keyword, page, pageSize)
                    .getOrThrow()
                    .toAddresses()

            val prevKey = if (page == PAGE_START) null else page - PAGE_OFFSET
            val nextKey = if (addresses.isEnd) null else page + PAGE_OFFSET

            LoadResult.Page(
                data = addresses.addresses,
                prevKey = prevKey,
                nextKey = nextKey,
            )
        }.getOrElse { throwable ->
            LoadResult.Error(throwable)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Address>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null
        return anchorPage.prevKey?.plus(PAGE_OFFSET) ?: anchorPage.nextKey?.minus(PAGE_OFFSET)
    }

    companion object {
        private const val PAGE_START = 1
        private const val PAGE_OFFSET = 1
    }
}
