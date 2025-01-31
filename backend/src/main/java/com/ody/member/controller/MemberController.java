package com.ody.member.controller;

import com.ody.common.annotation.AuthMember;
import com.ody.member.domain.Member;
import com.ody.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController implements MemberControllerSwagger {

    private final MemberService memberService;

    @Override
    @DeleteMapping("/members")
    public ResponseEntity<Void> delete(@AuthMember Member member) {
        memberService.delete(member);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Override
    @DeleteMapping("/v2/members")
    public ResponseEntity<Void> deleteV2(@AuthMember Member member) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

}
