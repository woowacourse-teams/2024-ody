package com.ody.auth.domain.logincontext;

import com.ody.member.domain.Member;
import java.util.Optional;

public interface LoginContext {

    boolean match(
            Optional<Member> sameDeviceMember,
            Optional<Member> sameProviderIdMember,
            Member requestMember
    );

    Member syncDevice(
            Optional<Member> sameDeviceMember,
            Optional<Member> sameProviderIdMember,
            Member requestMember
    );
}
