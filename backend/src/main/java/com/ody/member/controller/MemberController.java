package com.ody.member.controller;

import com.ody.auth.JwtTokenProvider;
import com.ody.auth.token.AccessToken;
import com.ody.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController implements MemberControllerSwagger {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @DeleteMapping("/members")
    public ResponseEntity<Void> delete(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        long memberId = jwtTokenProvider.parseAccessToken(new AccessToken(authorization));
        memberService.delete(memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
