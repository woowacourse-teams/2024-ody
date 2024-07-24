package com.ody.member.repository;

import com.ody.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ody.member.domain.DeviceToken;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByDeviceToken(DeviceToken deviceToken);
}
