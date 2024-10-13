package com.ody.route.controller;

import com.ody.route.dto.ApiCallCountResponse;
import com.ody.route.dto.ApiCallToggleResponse;
import com.ody.route.service.ApiCallService;
import com.ody.route.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApiCallController {

    private final ApiCallService apiCallService;
    private final RouteService routeService;

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

    @PostMapping("/admin/api-call/toggle/odsay")
    public ResponseEntity<ApiCallToggleResponse> toggleOdsayApiCall() {
        ApiCallToggleResponse apiCallToggleResponse = routeService.toggleOdsayApiCall();
        return ResponseEntity.ok(apiCallToggleResponse);
    }

    @PostMapping("/admin/api-call/toggle/google")
    public ResponseEntity<ApiCallToggleResponse> toggleGoogleApiCall() {
        ApiCallToggleResponse apiCallToggleResponse = routeService.toggleGoogleApiCall();
        return ResponseEntity.ok(apiCallToggleResponse);
    }
}
