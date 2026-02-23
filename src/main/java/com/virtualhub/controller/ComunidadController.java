package com.virtualhub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/comunidad")
public class ComunidadController {

    @GetMapping
    public String comunidad() {
        return "comunidad";
    }
}