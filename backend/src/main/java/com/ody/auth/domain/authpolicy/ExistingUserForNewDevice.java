package com.ody.auth.domain.authpolicy;

import com.ody.member.domain.Member;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ExistingUserForNewDevice implements AuthPolicy {

    @Override
    public boolean match(
            Optional<Member> sameDeviceMember,
            Optional<Member> samePidMember,
            Member requestMember
    ) {
        return sameDeviceMember.isEmpty() && samePidMember.isPresent();
    }

    @Override
    public Member authorize(Optional<Member> sameDeviceMember, Optional<Member> samePidMember, Member requestMember) {
        Member sameAuthProviderMember = samePidMember.get();
        sameAuthProviderMember.updateDeviceToken(requestMember.getDeviceToken());
        return sameAuthProviderMember;
    }
}
