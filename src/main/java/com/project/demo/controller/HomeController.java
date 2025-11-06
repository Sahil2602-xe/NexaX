package com.project.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping
    public String home(){
        return "Welcome to NexaX - Crypto Exchange Platform";
    }

    @GetMapping("/api")
    public String secure(){
        return "Welcome to NexaX - Crypto Exchange Platform - Secured";
    }
}
