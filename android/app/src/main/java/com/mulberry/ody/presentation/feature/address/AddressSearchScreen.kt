package com.mulberry.ody.presentation.feature.address

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.mulberry.ody.R
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.presentation.common.modifier.noRippleClickable
import com.mulberry.ody.presentation.component.OdyTextField
import com.mulberry.ody.presentation.component.OdyTopAppBar
import com.mulberry.ody.presentation.theme.Gray300
import com.mulberry.ody.presentation.theme.Gray350
import com.mulberry.ody.presentation.theme.OdyTheme

@Composable
fun AddressSearchScreen(
    onClickBack: () -> Unit,
    viewModel: AddressSearchViewModel = hiltViewModel(),
) {
    // pressed back key 추가
    var addressSearchKeyword by rememberSaveable { mutableStateOf("") }
    val addresses = viewModel.address.collectAsLazyPagingItems()

    Scaffold(
        containerColor = OdyTheme.colors.primary,
        topBar = {
            OdyTopAppBar(
                title = stringResource(id = R.string.address_search_toolbar_title),
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = null,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        AddressSearchContent(
            addressSearchKeyword = addressSearchKeyword,
            onChangedSearchKeyword = {
                addressSearchKeyword = it
                if (addressSearchKeyword.isEmpty()) {
                    viewModel.clearAddresses()
                }
           },
            onSearchAddress = { viewModel.searchAddress(addressSearchKeyword) },
            onClearSearchKeyword = {
                addressSearchKeyword = ""
                viewModel.clearAddresses()
            },
            addresses = addresses.toList(),
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun AddressSearchContent(
    addressSearchKeyword: String,
    onChangedSearchKeyword: (String) -> Unit,
    onSearchAddress: () -> Unit,
    onClearSearchKeyword: () -> Unit,
    addresses: List<Address>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        AddressSearchTextField(
            addressSearchKeyword = addressSearchKeyword,
            onChangedSearchKeyword = onChangedSearchKeyword,
            onSearchAddress = onSearchAddress,
            onClearSearchKeyword = onClearSearchKeyword,
            modifier =
            Modifier
                .padding(top = 36.dp)
                .padding(horizontal = 40.dp),
        )
        Box(
            modifier =
                Modifier
                    .padding(top = 32.dp)
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(Gray300),
        )
        if (addresses.isEmpty()) {
            EmptyAddress()
        } else {
            AddressList(addresses)
        }
    }
}

@Composable
private fun AddressSearchTextField(
    addressSearchKeyword: String,
    onSearchAddress: () -> Unit,
    onChangedSearchKeyword: (String) -> Unit,
    onClearSearchKeyword: () -> Unit,
    modifier: Modifier = Modifier
) {
    OdyTextField(
        value = addressSearchKeyword,
        onValueChange = onChangedSearchKeyword,
        placeholder = stringResource(id = R.string.address_question_hint),
        modifier = modifier,
        trailingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_round_cancel),
                tint = Color.Unspecified,
                modifier = Modifier.noRippleClickable { onClearSearchKeyword() },
                contentDescription = null,
            )
        },
        keyboardType = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search,
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearchAddress() }
        )
    )
}

@Composable
private fun EmptyAddress() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(id = R.string.address_search_empty),
            style = OdyTheme.typography.pretendardMedium20.copy(color = Gray350),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun AddressList(addresses: List<Address>) {
    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 26.dp),
        contentPadding = PaddingValues(vertical = 14.dp),
    ) {
        items(items = addresses, key = { it.id }) {
            AddressItem(it)
        }
    }
}

@Composable
private fun AddressItem(address: Address) {
    Column {
        Row(modifier = Modifier.padding(vertical = 18.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_location_on),
                tint = OdyTheme.colors.quarternary,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = address.placeName,
                    style = OdyTheme.typography.pretendardBold20.copy(color = OdyTheme.colors.secondary),
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                Text(
                    text = address.detailAddress,
                    style = OdyTheme.typography.pretendardRegular16.copy(color = OdyTheme.colors.quinary),
                )
            }
        }
        HorizontalDivider()
    }
}

@Composable
@Preview(showSystemUi = true)
fun AddressSearchContentPreview() {
    OdyTheme {
        AddressSearchContent(
            addressSearchKeyword = "사당역",
            onSearchAddress = {},
            onChangedSearchKeyword = {},
            onClearSearchKeyword = {},
            addresses =
                listOf(
                    Address(
                        id = 1,
                        placeName = "사당역 2호선",
                        detailAddress = "서울 동작구 남부순환로 2089",
                        longitude = "",
                        latitude = "",
                    ),
                    Address(
                        id = 2,
                        placeName = "사당역 4호선",
                        detailAddress = "서울 동작구 동작대로 3 사당역",
                        longitude = "",
                        latitude = "",
                    ),
                ),
        )
    }
}

fun <T : Any> LazyPagingItems<T>.toList(): List<T> {
    val list = mutableListOf<T>()
    repeat(itemCount) {
        val item = get(it) ?: return@repeat
        list.add(item)
    }
    return list
}
