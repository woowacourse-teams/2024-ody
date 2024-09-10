package com.ody.common;

import com.ody.auth.AuthProperties;
import com.ody.auth.token.AccessToken;
import com.ody.auth.token.RefreshToken;

public class TokenFixture {

    public static AuthProperties authPropertiesForValidToken = new AuthProperties(
            "accessKey",
            "refreshKey",
            60000,
            60000
    );

    public static AuthProperties authPropertiesForValidToken2 = new AuthProperties(
            "accessKey",
            "refreshKey",
            30000,
            30000
    );

    private static AuthProperties authPropertiesForExpiredToken = new AuthProperties(
            authPropertiesForValidToken.getAccessKey(),
            authPropertiesForValidToken.getRefreshKey(),
            0,
            0
    );

    private static AuthProperties authPropertiesForInvalidToken = new AuthProperties(
            "wrongAccessKey",
            "wrongRefreshKey",
            60000,
            60000
    );

    public static AuthProperties getAuthPropertiesForValidToken() {
        return authPropertiesForValidToken;
    }

    public static AccessToken getValidAccessToken(long memberId) {
        return new AccessToken(memberId, authPropertiesForValidToken);
    }

    public static AccessToken getExpiredAccessToken(long memberId) {
        return new AccessToken(memberId, authPropertiesForExpiredToken);
    }

    public static AccessToken getInvalidAccessToken(long memberId) {
        return new AccessToken(memberId, authPropertiesForInvalidToken);
    }

    public static RefreshToken getValidRefreshToken() {
        return new RefreshToken(authPropertiesForValidToken);
    }

    public static RefreshToken getRefreshToken(AuthProperties authProperties) {
        return new RefreshToken(authProperties);
    }

    public static RefreshToken getExpiredRefreshToken() {
        return new RefreshToken(authPropertiesForExpiredToken);
    }

    public static RefreshToken getInvalidRefreshToken() {
        return new RefreshToken(authPropertiesForInvalidToken);
    }

    private TokenFixture() {
    }
}
