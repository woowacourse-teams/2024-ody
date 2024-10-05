package com.ody.route.controller;

import com.ody.route.dto.ApiCallCountResponse;
import com.ody.route.service.ApiCallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApiCallController {

    private final ApiCallService apiCallService;

    @GetMapping("/admin/api-call/count/odsay")
    public ResponseEntity<ApiCallCountResponse> countOdsayApiCall() {
        ApiCallCountResponse apiCallCountResponse = apiCallService.countOdsayApiCall();
        return ResponseEntity.ok(apiCallCountResponse);
    }

    @GetMapping("/admin/api-call/count/google")
    public ResponseEntity<ApiCallCountResponse> countGoogleApiCall() {
        ApiCallCountResponse apiCallCountResponse = apiCallService.countGoogleApiCall();
        return ResponseEntity.ok(apiCallCountResponse);
    }
}
