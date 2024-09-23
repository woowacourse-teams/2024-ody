package com.ody.member.repository;

import com.ody.member.domain.AuthProvider;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByAuthProvider(AuthProvider authProvider);

    Optional<Member> findByDeviceToken(DeviceToken deviceToken);
}
