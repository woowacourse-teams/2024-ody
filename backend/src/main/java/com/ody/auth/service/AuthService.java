package com.ody.auth.service;

import com.ody.auth.JwtTokenProvider;
import com.ody.auth.dto.response.AuthResponse;
import com.ody.auth.token.AccessToken;
import com.ody.auth.token.RefreshToken;
import com.ody.common.exception.OdyUnauthorizedException;
import com.ody.member.domain.Member;
import com.ody.member.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    public AuthResponse issueToken(Member member) {
        AccessToken accessToken = jwtTokenProvider.createAccessToken(member);
        RefreshToken refreshToken = jwtTokenProvider.createRefreshToken();
        return new AuthResponse(accessToken, refreshToken);
    }

    public Member parseAccessToken(String rawAccessToken) {
        AccessToken accessToken = new AccessToken(rawAccessToken);
        if (!jwtTokenProvider.validateAccessToken(accessToken)) {
            throw new OdyUnauthorizedException("유효하지 않은 액세스 토큰입니다.");
        }
        String memberId = jwtTokenProvider.parseAccessToken(accessToken);
        return memberService.findById(Long.parseLong(memberId));
    }
}
