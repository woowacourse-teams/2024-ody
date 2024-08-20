package com.ody.member.domain;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthProvider that = (AuthProvider) o;
        return getProviderType() == that.getProviderType() && Objects.equals(getProviderId(), that.getProviderId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProviderType(), getProviderId());
    }
}
