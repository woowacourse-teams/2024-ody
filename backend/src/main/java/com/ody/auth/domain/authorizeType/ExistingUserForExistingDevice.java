package com.ody.auth.domain.authorizeType;

import com.ody.member.domain.Member;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ExistingUserForExistingDevice implements AuthorizationType {

    @Override
    public boolean match(Optional<Member> sameDeviceMember, Optional<Member> samePidMember, Member requestMember) {
        return sameDeviceMember.isPresent()
                && samePidMember.isPresent()
                && requestMember.isSame(sameDeviceMember.get());
    }

    @Override
    public Member authorize(Optional<Member> sameDeviceMember, Optional<Member> samePidMember, Member requestMember) {
        return samePidMember.get();
    }
}
