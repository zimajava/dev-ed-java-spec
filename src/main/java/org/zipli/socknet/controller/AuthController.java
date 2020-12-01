package org.zipli.socknet.controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.zipli.socknet.models.User;

@RestController
@RequestMapping("/zipli/auth")
public class AuthController {
    @GetMapping("/sign-up")
    public String registration(Model model) {
        // method realization
        return "registration";
    }

    @PostMapping("/sign-up")
    public String addUser(@ModelAttribute("userForm") @Validated User userForm, BindingResult bindingResult, Model model) {
        //method realization
        return "/redirect:/";
    }

    @PostMapping("/sign-in")
    public String authenticate() {
        //method realization
        return "/";
    }
}
