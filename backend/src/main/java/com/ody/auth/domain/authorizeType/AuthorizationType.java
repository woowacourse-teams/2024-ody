package com.ody.auth.domain.authorizeType;

import com.ody.member.domain.Member;
import java.util.Optional;

public interface AuthorizationType {

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
