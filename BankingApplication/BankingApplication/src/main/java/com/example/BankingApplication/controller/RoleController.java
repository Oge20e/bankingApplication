package com.example.BankingApplication.controller;

import com.example.BankingApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@Controller
public class RoleController {
    @Autowired
    private UserService userService;

    @GetMapping("/assignRoles")
    public String showRoleAssignmentForm() {
        return "assignRoles";
    }

    @PostMapping("/assignRoles")
    public String assignRoles(@RequestParam String username, @RequestParam String roles) {
        userService.findByUsername(username).ifPresent(user -> {
            user.setRoles(Set.of(roles.split(",")));
            userService.save(user);
        });
        return "redirect:/users";
    }
}
