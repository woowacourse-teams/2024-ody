package com.mulberry.ody.data.remote.thirdparty.address

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mulberry.ody.domain.apiresult.getOrThrow
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.repository.location.AddressRepository

class AddressPagingSource(
    private val keyword: String,
    private val addressRepository: AddressRepository,
) : PagingSource<Int, Address>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Address> {
        val page = params.key ?: 1
        return runCatching {
            val address =
                addressRepository.fetchAddresses(
                    keyword = keyword,
                    page = page,
                    pageSize = PAGE_SIZE,
                ).getOrThrow()

            val prevKey = if (page == 1) null else page - PAGE_OFFSET
            val nextKey = if (address.isEnd) null else page + PAGE_OFFSET

            LoadResult.Page(
                data = address.addresses,
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
        private const val PAGE_OFFSET = 1
        private const val PAGE_SIZE = 10
    }
}
