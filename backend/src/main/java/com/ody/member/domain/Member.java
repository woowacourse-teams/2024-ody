package com.ody.member.domain;

import com.ody.auth.token.RefreshToken;
import com.ody.mate.domain.Nickname;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@Filter(name = "deletedMemberFilter", condition = "deleted_at IS NULL")
@FilterDef(name = "deletedMemberFilter")
@SQLDelete(sql = "UPDATE member SET deleted_at = NOW() WHERE id = ?")
@Table(indexes = {
        @Index(name = "index_member_device_token", columnList = "device_token"),
        @Index(name = "index_member_auth_provider", columnList = "provider_type, provider_id")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Embedded
    private AuthProvider authProvider;

    @NotNull
    private Nickname nickname;

    @NotNull
    private String imageUrl;

    @Embedded
    private DeviceToken deviceToken;

    @Embedded
    private RefreshToken refreshToken;

    private LocalDateTime deletedAt;

    public Member(String providerId, Nickname nickname, String imageUrl, DeviceToken deviceToken) {
        this(null, new AuthProvider(providerId), nickname, imageUrl, deviceToken, null, null);
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
