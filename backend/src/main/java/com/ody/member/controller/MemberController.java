package com.ody.member.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class MemberController implements MemberControllerSwagger {

    @Override
    public ResponseEntity<Void> save(@RequestHeader(HttpHeaders.AUTHORIZATION) String fcmToken) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
