package com.virtualhub.controller;

import com.virtualhub.service.JuegoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/juego")
public class JuegoController {

    public JuegoController(JuegoService juegoService) {
    }

}