package com.ody.route.controller;

import com.ody.route.dto.ApiCallCountResponse;
import com.ody.route.dto.ApiCallStateResponse;
import com.ody.route.mapper.RouteClientMapper;
import com.ody.route.service.ApiCallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/admin/api-call/toggle/{clientName}")
    public ResponseEntity<Void> toggleApiCallState(@PathVariable String clientName) {
        apiCallService.toggleStateByClientType(RouteClientMapper.from(clientName));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/api-call/state/{clientName}")
    public ResponseEntity<ApiCallStateResponse> getApiCallState(@PathVariable String clientName) {
        boolean state = apiCallService.findStateByClientType(RouteClientMapper.from(clientName));
        return ResponseEntity.ok(new ApiCallStateResponse(state));
    }
}
