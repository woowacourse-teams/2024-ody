package com.ody.auth.domain.authpolicy;

import com.ody.member.domain.Member;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class NewUserForNewDevice implements AuthPolicy {

    @Override
    public boolean match(
            Optional<Member> sameDeviceMember,
            Optional<Member> samePidMember,
            Member requestMember
    ) {
        return sameDeviceMember.isEmpty()
                && samePidMember.isEmpty();
    }

    @Override
    public Member authorize(
            Optional<Member> sameDeviceMember,
            Optional<Member> samePidMember,
            Member requestMember
    ) {
        return requestMember;
    }
}
