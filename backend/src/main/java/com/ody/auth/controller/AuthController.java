package com.ody.auth.controller;

import com.ody.auth.dto.request.AppleAuthRequest;
import com.ody.auth.dto.request.KakaoAuthRequest;
import com.ody.auth.dto.response.AuthResponse;
import com.ody.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerSwagger {

    private final AuthService authService;

    @Override
    @PostMapping("/v1/auth/kakao")
    public ResponseEntity<AuthResponse> authKakao(@Valid @RequestBody KakaoAuthRequest kakaoAuthRequest) {
        AuthResponse authResponse = authService.authenticate(kakaoAuthRequest);
        return ResponseEntity.ok(authResponse);
    }

    @Override
    @PostMapping("/v1/auth/apple")
    public ResponseEntity<AuthResponse> authApple(@Valid @RequestBody AppleAuthRequest appleAuthRequest) {
        AuthResponse authResponse = authService.authenticate(appleAuthRequest);
        return ResponseEntity.ok(authResponse);
    }

    @Override
    @PostMapping("/v1/auth/refresh")
    public ResponseEntity<AuthResponse> refreshAccessToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        AuthResponse authResponse = authService.renewTokens(authorization);
        return ResponseEntity.ok(authResponse);
    }

    @Override
    @PostMapping("/v1/auth/logout")
    public ResponseEntity<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String rawAccessTokenValue) {
        authService.logout(rawAccessTokenValue);
        return ResponseEntity.ok().build();
    }
}
