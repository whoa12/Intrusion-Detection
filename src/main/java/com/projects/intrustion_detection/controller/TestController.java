package com.projects.intrustion_detection.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/ping")
    public String ping(@RequestParam String q) {
        return "OK";
    }

    @PostMapping("/pingpost")
    public String pingPost() {
        return "OK";
    }



}
