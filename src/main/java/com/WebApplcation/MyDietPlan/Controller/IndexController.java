package com.WebApplcation.MyDietPlan.Controller;

import org.springframework.web.bind.annotation.GetMapping;

public class IndexController {
    

    @GetMapping("/")
    public String index() {
        return "index";
}

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}