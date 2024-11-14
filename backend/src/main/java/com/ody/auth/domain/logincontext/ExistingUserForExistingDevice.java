package com.ody.auth.domain.logincontext;

import com.ody.member.domain.Member;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ExistingUserForExistingDevice implements LoginContext {

    @Override
    public boolean match(
            Optional<Member> sameDeviceMember,
            Optional<Member> sameProviderIdMember,
            Member requestMember
    ) {
        return sameDeviceMember.isPresent()
                && sameProviderIdMember.isPresent()
                && requestMember.isSame(sameDeviceMember.get());
    }

    @Override
    public Member syncDevice(
            Optional<Member> sameDeviceMember,
            Optional<Member> sameProviderIdMember,
            Member requestMember
    ) {
        return sameProviderIdMember.get();
    }
}
