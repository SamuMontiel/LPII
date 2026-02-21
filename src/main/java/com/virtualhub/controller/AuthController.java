package com.virtualhub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
//wawawawa
@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
