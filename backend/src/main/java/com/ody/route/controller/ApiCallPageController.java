package com.ody.route.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ApiCallPageController {

    @GetMapping("${api-call.page}")
    public String apiCallPage() {
        return "api-call";
    }
}
