package com.ody.mate.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Nickname {

//    private static final int MIN_LENGTH = 1;
//    private static final int MAX_LENGTH = 10;

    @Column(name = "nickname")
    private String value;

    public Nickname(String value) {
//        validateLength(value);
        this.value = value;
    }

//    private void validateLength(String value) {
//        if (value.isBlank() || value.length() >= MAX_LENGTH) {
//            String message = String.format("닉네임은 %d글자 이상, %d자 미만으로 입력 가능합니다.", MIN_LENGTH, MAX_LENGTH);
//            throw new OdyBadRequestException(message);
//        }
//    }
}
