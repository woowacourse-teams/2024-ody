package com.ody.auth.token;

import com.ody.auth.AuthProperties;

public interface Token {

    String getSecretKey(AuthProperties authProperties);

    String getValue();
}
