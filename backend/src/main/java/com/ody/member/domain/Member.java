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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "uniqueProviderTypeAndProviderId",
                columnNames = {"providerType", "providerId"}
        ),
        @UniqueConstraint(
                name = "uniqueDeviceToken",
                columnNames = {"deviceToken"}
        ),
})
@Filter(name = "deletedMemberFilter", condition = "deleted_at IS NOT NULL or deleted_at IS NULL")
@FilterDef(name = "deletedMemberFilter")
@SQLDelete(sql = "UPDATE member SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
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

    @Embedded
    private DeviceToken deviceToken;

    @Embedded
    private RefreshToken refreshToken;

    private LocalDateTime deletedAt;

    public Member(String providerId, String nickname, String imageUrl, DeviceToken deviceToken) {
        this(null, new AuthProvider(providerId), nickname, imageUrl, deviceToken, null, null);
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

    public boolean isLogout() {
        return this.refreshToken == null;
    }

    public boolean isSame(AuthProvider otherAuthProvider) {
        return this.authProvider.equals(otherAuthProvider);
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
}
