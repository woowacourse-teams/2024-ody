package com.ody.member.repository;

import com.ody.member.domain.AuthProvider;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findFirstByDeviceToken(DeviceToken deviceToken);

    boolean existsByAuthProvider(AuthProvider authProvider);

    Optional<Member> findByAuthProvider(AuthProvider authProvider);
}
