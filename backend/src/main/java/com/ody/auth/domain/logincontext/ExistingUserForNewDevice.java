package com.ody.auth.domain.logincontext;

import com.ody.member.domain.Member;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ExistingUserForNewDevice implements LoginContext {

    @Override
    public boolean match(
            Optional<Member> sameDeviceMember,
            Optional<Member> sameProviderIdMember,
            Member requestMember
    ) {
        return sameDeviceMember.isEmpty()
                && sameProviderIdMember.isPresent();
    }

    @Override
    public Member syncDevice(
            Optional<Member> sameDeviceMember,
            Optional<Member> sameProviderIdMember,
            Member requestMember
    ) {
        Member sameAuthProviderMember = sameProviderIdMember.get();
        sameAuthProviderMember.updateDeviceToken(requestMember.getDeviceToken());
        return sameAuthProviderMember;
    }
}
