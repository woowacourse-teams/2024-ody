package com.ody.route.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Profile("!test")
@Controller
@RequiredArgsConstructor
public class ApiCallPageController {

    @GetMapping("${api-call.page}")
    public String apiCallPage() {
        return "api-call";
    }
}
