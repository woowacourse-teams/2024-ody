package com.ody.route.controller;

import com.ody.route.dto.ApiCallCountResponse;
import com.ody.route.dto.ApiCallEnabledResponse;
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

    @GetMapping("/admin/api-call/count/{clientName}")
    public ResponseEntity<ApiCallCountResponse> countApiCall(@PathVariable String clientName) {
        ApiCallCountResponse apiCallCountResponse = apiCallService.countApiCall(RouteClientMapper.from(clientName));
        return ResponseEntity.ok(apiCallCountResponse);
    }

    @PostMapping("/admin/api-call/toggle/{clientName}")
    public ResponseEntity<Void> toggleApiCallEnabled(@PathVariable String clientName) {
        apiCallService.toggleApiCallEnabled(RouteClientMapper.from(clientName));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/api-call/enabled/{clientName}")
    public ResponseEntity<ApiCallEnabledResponse> getApiCallEnabled(@PathVariable String clientName) {
        ApiCallEnabledResponse response = apiCallService.getApiCallEnabled(RouteClientMapper.from(clientName));
        return ResponseEntity.ok(response);
    }
}
