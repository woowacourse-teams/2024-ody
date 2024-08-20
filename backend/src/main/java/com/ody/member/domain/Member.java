package com.ody.member.domain;

import com.ody.auth.token.RefreshToken;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "uniqueProviderAndProviderId",
                columnNames = {"providerType", "providerId"}
        ),
        @UniqueConstraint(
                name = "uniqueDeviceToken",
                columnNames = {"deviceToken"}
        ),
})
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Embedded
    private AuthProvider authProvider;

    @NotNull
    private String nickname;

    @NotNull
    private String imageUrl;

    @NotNull
    @Embedded
    private DeviceToken deviceToken;

    @Embedded
    private RefreshToken refreshToken;

    public Member(DeviceToken deviceToken) { // TODO: 제거
        this(null, new AuthProvider("1234"), "ahdzlrjsdn", "image", deviceToken, new RefreshToken("rt"));
    }

    public Member(String providerId, String nickname, String imageUrl, DeviceToken deviceToken) {
        this(null, new AuthProvider(providerId), nickname, imageUrl, deviceToken, null);
    }

    public String getDeviceTokenValue() {
        return deviceToken.getValue();
    }

    public ProviderType getProviderType() {
        return authProvider.getProviderType();
    }

    public String getProviderId() {
        return authProvider.getProviderId();
    }

    public boolean isSame(RefreshToken otherRefreshToken) {
        return this.refreshToken.equals(otherRefreshToken);
    }

    public void updateRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateDeviceTokenNull() {
        updateDeviceToken(null);
    }

    public void updateDeviceToken(DeviceToken deviceToken) {
        this.deviceToken = deviceToken;
    }

    public boolean isSame(AuthProvider otherAuthProvider) {
        return this.authProvider.equals(otherAuthProvider);
    }
}
