package com.ody.member.domain;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AuthProvider {

    @NotNull
    private ProviderType providerType;

    @NotNull
    private String providerId;

    public AuthProvider(String providerId) {
        this(ProviderType.KAKAO, providerId);
    }
}
