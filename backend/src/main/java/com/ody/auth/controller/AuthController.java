package com.ody.auth.controller;

import com.ody.auth.dto.request.AuthRequest;
import com.ody.auth.dto.response.AuthResponse;
import com.ody.member.domain.Member;
import com.ody.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerSwagger {

    private final MemberService memberService;

    @Override
    @PostMapping("/v1/auth/kakao")
    public ResponseEntity<AuthResponse> authKakao(@Valid AuthRequest authRequest) {
        AuthResponse authResponse = memberService.save(authRequest);
        return ResponseEntity.ok(authResponse);
    }

    @Override
    @PostMapping("/v1/auth/refresh")
    public ResponseEntity<AuthResponse> refreshAccessToken(Member member) {
        return null;
    }
}
