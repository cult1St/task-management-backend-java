package com.task_management.first_backend.application.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(HttpServletRequest request){
        String acceptHeader = request.getHeader("Accept");
        if(acceptHeader != null && acceptHeader.contains("text/html")) {
            return "redirect:/swagger";
        }
        return "forward:/api";
    }
}
