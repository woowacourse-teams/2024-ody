package com.ody.auth.service;

import com.ody.auth.JwtTokenProvider;
import com.ody.auth.domain.AuthorizationHeader;
import com.ody.auth.dto.request.AuthRequest;
import com.ody.auth.dto.response.AuthResponse;
import com.ody.auth.token.AccessToken;
import com.ody.auth.token.RefreshToken;
import com.ody.common.exception.OdyBadRequestException;
import com.ody.common.exception.OdyUnauthorizedException;
import com.ody.member.domain.Member;
import com.ody.member.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    public Member parseAccessToken(String rawAccessToken) {
        AccessToken accessToken = new AccessToken(rawAccessToken);
        jwtTokenProvider.validate(accessToken);
        long memberId = jwtTokenProvider.parseAccessToken(accessToken);
        return memberService.findById(memberId);
    }

    @Transactional
    public AuthResponse issueTokens(AuthRequest authRequest) {
        Member member = memberService.save(authRequest.toMember());
        return issueNewTokens(member.getId());
    }

    @Transactional
    public AuthResponse renewTokens(String rawAuthorizationHeader) {
        AuthorizationHeader authorizationHeader = new AuthorizationHeader(rawAuthorizationHeader);
        AccessToken accessToken = authorizationHeader.getAccessToken();
        RefreshToken refreshToken = authorizationHeader.getRefreshToken();

        if (jwtTokenProvider.isUnexpired(accessToken)) {
            return new AuthResponse(accessToken, refreshToken);
        }

        jwtTokenProvider.validate(refreshToken);
        long memberId = jwtTokenProvider.parseAccessToken(accessToken);
        checkSameMemberToken(memberId, refreshToken);
        return issueNewTokens(memberId);
    }

    private AuthResponse issueNewTokens(long memberId) {
        AccessToken accessToken = jwtTokenProvider.createAccessToken(memberId);
        RefreshToken refreshToken = jwtTokenProvider.createRefreshToken();
        memberService.updateRefreshToken(memberId, refreshToken);
        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional
    public void logout(String rawAuthorizationHeader) {
        AuthorizationHeader authorizationHeader = new AuthorizationHeader(rawAuthorizationHeader);
        AccessToken accessToken = authorizationHeader.getAccessToken();
        RefreshToken refreshToken = authorizationHeader.getRefreshToken();

        checkTokenExpiration(accessToken, refreshToken);

        long memberId = jwtTokenProvider.parseAccessToken(accessToken);
        checkSameMemberToken(memberId, refreshToken);
        memberService.updateRefreshToken(memberId, null);
    }

    private void checkTokenExpiration(AccessToken accessToken, RefreshToken refreshToken) {
        try {
            jwtTokenProvider.validate(accessToken);
            jwtTokenProvider.validate(refreshToken);
        } catch (OdyUnauthorizedException exception) {
            throw new OdyBadRequestException(exception.getMessage());
        }
    }

    private void checkSameMemberToken(long memberId, RefreshToken refreshToken) {
        if (!memberService.isMemberRefreshToken(memberId, refreshToken)) {
            throw new OdyBadRequestException("리프레시 토큰 정보가 일치하지 않습니다");
        }
    }
}
