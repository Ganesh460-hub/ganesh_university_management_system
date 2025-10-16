package com.ums.ums_project.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {

    @GetMapping("/redirect")
    public String redirectAfterLogin(Authentication auth){
        if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"))){
            return "redirect:/student/dashboard";
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_TEACHER"))) {
            return "redirect:/teacher/dashboard";
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            return "redirect:/admin/dashboard";
        }
        return "redirect:/login";
    }
}
