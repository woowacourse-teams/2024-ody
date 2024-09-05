package com.ody.member.controller;

import com.ody.member.domain.DeviceToken;
import com.ody.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController implements MemberControllerSwagger {

    private final MemberService memberService;

    @Override
    @PostMapping("/members")
    public ResponseEntity<Void> save(@RequestHeader("Authorization") String authorization) {
        memberService.save(new DeviceToken(authorization));
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @Override
    @DeleteMapping("/members")
    public ResponseEntity<Void> delete(String authorization) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
