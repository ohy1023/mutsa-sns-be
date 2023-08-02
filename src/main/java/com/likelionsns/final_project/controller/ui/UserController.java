package com.likelionsns.final_project.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {


    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    @GetMapping("/register")
    public String register() {
        return "user/register";
    }

}
