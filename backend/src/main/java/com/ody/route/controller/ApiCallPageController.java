package com.ody.route.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ApiCallPageController {

    @GetMapping("/api-call")
    public String getCountData2325(Model model) {
        model.addAttribute("enabled1", true); // TODO: 제어 설정 조회 API 구현 및 호출
        model.addAttribute("enabled2", false);
        model.addAttribute("enabled3", true);
        model.addAttribute("enabled4", true);

        return "api-call";
    }
}
