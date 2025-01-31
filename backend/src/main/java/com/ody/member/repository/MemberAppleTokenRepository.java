package com.ody.member.repository;

import com.ody.member.domain.AuthProvider;
import com.ody.member.domain.MemberAppleToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberAppleTokenRepository extends JpaRepository<MemberAppleToken, Long> {

    Optional<MemberAppleToken> findByMemberId(long memberId);

    Optional<MemberAppleToken> findByMember_AuthProvider(AuthProvider authProvider);

    void deleteByMember_AuthProvider(AuthProvider authProvider);
}
