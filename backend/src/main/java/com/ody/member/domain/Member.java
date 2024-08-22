package com.ody.member.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String nickname;

    @Embedded
    @NotNull
    private DeviceToken deviceToken;

    public Member(DeviceToken deviceToken) {
        this(null, "회원 닉네임", deviceToken);
    }

    public Member(String nickname, DeviceToken deviceToken) {
        this(null, nickname, deviceToken);
    }
}
