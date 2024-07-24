package com.ody.member.controller;

import com.ody.common.annotaion.AuthMember;
import com.ody.member.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class MemberController implements MemberControllerSwagger {

    @Override
    @PostMapping("/members")
    public ResponseEntity<Void> save(@AuthMember Member member) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }
}
