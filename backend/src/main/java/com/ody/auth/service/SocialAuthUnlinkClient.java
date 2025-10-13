package com.ody.auth.service;

import com.ody.member.domain.Member;
import com.ody.member.domain.ProviderType;

public interface SocialAuthUnlinkClient {

    void unlink(Member member);

    ProviderType getProviderType();
}
