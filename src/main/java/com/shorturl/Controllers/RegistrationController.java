package com.shorturl.Controllers;

import com.shorturl.Exceptions.UsernameExistsException;
import com.shorturl.Service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password, Model model) {
        if(userService.existsUser(username)) {
            throw new UsernameExistsException("Selected username is already in use");
        }
        userService.registerUser(username, password);
        model.addAttribute("message", "User registered successfully");
        return "login";
    }
}
