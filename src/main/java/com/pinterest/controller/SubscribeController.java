package com.pinterest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("subscribes")
public class SubscribeController {

    @GetMapping
    public String subscribe() {
        return "subscribes/index";
    }
}
