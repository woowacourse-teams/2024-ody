package com.ody.auth.service;

import com.ody.member.domain.ProviderType;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SocialAuthUnlinkClientFactory {

    private final Map<ProviderType, SocialAuthUnlinkClient> clients;

    @Autowired
    public SocialAuthUnlinkClientFactory(List<SocialAuthUnlinkClient> socialAuthUnlinkClients) {
        this.clients = socialAuthUnlinkClients.stream()
                .collect(Collectors.toMap(SocialAuthUnlinkClient::getProviderType, Function.identity()));
    }

    public SocialAuthUnlinkClient getClient(ProviderType providerType) {
        return clients.get(providerType);
    }
}
