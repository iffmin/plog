package com.plog.PLOG.controller;

import com.plog.PLOG.dto.dto;
import com.plog.PLOG.service.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class controller {

    @Autowired
    public service s;



    @GetMapping("/")
    public String home(Model model){

        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        model.addAttribute("name", name);

        return "home";
    }

    @GetMapping("/login")
    public String login(){
        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/signup")
    public String signup(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)) {

            return "redirect:/";
        }
        return "signup";
    }

    @PostMapping("/signupProc")
    public String signupproc(dto d){

        s.toEntity(d);

        return "redirect:/login";
    }

    @GetMapping("/user/update/{username}")
    public String updatePage(@PathVariable("username") String username, Model model) {

        if (s.isAccess(username)) {
            dto dto = s.readOneUser(username);
            model.addAttribute("USER", dto);
            return "update";
        }
        return "redirect:/login";
    }

    @PostMapping("/user/update/{username}")
    public String updateProcess(@PathVariable("username") String username, dto updatedDto, Model model) {

        if (s.isAccess(username)) {

            s.updateOneUser(updatedDto, username);
            return "redirect:/";
        }
        return "redirect:/login";
    }
}
