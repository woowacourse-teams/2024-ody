package com.ody.mate.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class NickNameTest {

    @DisplayName("닉네임이 1글자 이상, 10자 미만이면 정상적으로 생성된다")
    @ParameterizedTest
    @ValueSource(strings = {"k", "abcdefghi"})
    void createNickNameSuccess(String nickName) {
        assertThatCode(() -> new NickName(nickName))
                .doesNotThrowAnyException();
    }

    @DisplayName("닉네임이 1글자 미만, 10자 이상이면 예외가 발생한다")
    @ParameterizedTest
    @ValueSource(strings = {"", "abcdefghij"})
    void createNickNameException(String nickName) {
        assertThatThrownBy(() -> new NickName(nickName))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
