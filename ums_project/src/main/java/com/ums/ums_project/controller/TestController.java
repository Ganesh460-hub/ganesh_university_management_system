package com.ums.ums_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TestController {

    @GetMapping("/attendance/{date}")
    public String testDate(@PathVariable String date, Model model) {
        System.out.println("Date clicked: " + date);
        model.addAttribute("message", "You clicked on: " + date);
        return "test-page"; // Create a simple test-page.html
    }
}