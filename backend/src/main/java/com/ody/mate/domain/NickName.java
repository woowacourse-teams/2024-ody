package com.ody.mate.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NickName {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 10;

    private String nickName;

    public NickName(String nickName) {
        validateLength(nickName);
        this.nickName = nickName;
    }

    private void validateLength(String nickName) {
        if (nickName.length() < MIN_LENGTH || nickName.length() >= MAX_LENGTH) {
            String message = String.format("닉네임은 %d글자 이상, %d자 미만으로 입력 가능합니다.", MIN_LENGTH, MAX_LENGTH);
            throw new IllegalArgumentException(message);
        }
    }
}
