package com.ody.auth.service;

import com.ody.member.domain.ProviderType;

public interface SocialAuthUnlinkClient {

    void unlink(String providerId);

    ProviderType getProviderType();
}
