package com.woowacourse.ody.presentation.joininfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class JoinInfoViewModel : ViewModel() {
    val nickname: MutableLiveData<String> = MutableLiveData()
    val nicknameLength: LiveData<Int> = nickname.map { it.length }
    val hasNickname: LiveData<Boolean> = nickname.map { it.isNotEmpty() }

    fun emptyNickname() {
        nickname.value = ""
    }

    companion object {
        const val NICK_NAME_MAX_LENGTH = 9
    }
}
