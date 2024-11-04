package com.ody.auth.domain.authpolicy;

import com.ody.member.domain.Member;
import java.util.Optional;

public interface AuthPolicy {

    boolean match(
            Optional<Member> sameDeviceMember,
            Optional<Member> samePidMember,
            Member requestMember
    );

    Member authorize(
            Optional<Member> sameDeviceMember,
            Optional<Member> samePidMember,
            Member requestMember
    );
}
