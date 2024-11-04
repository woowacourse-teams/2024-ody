package com.ody.auth.token;

import com.ody.auth.AuthProperties;

public interface JwtToken {

    String getSecretKey(AuthProperties authProperties);

    String getValue();
}
