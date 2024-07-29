package com.example.BankingApplication.controller;

import com.example.BankingApplication.entity.BankAccount;
import com.example.BankingApplication.service.BankingService;
import com.example.BankingApplication.service.impl.BankAccountServiceImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.List;

@Controller
public class BankingController {
    @Autowired
    private BankingService service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String index(Model model) {
        List<BankAccount> accounts = service.getAllAccounts();
        model.addAttribute("accounts", accounts);
        return "index";
    }

    @GetMapping("/create")
    public String createAccountForm() {
        return "createAccount";
    }

    @PostMapping("/create")
    public String createAccount(@RequestParam String accountHolderName,
                                @RequestParam int pin,
                                Model model) {
        BankAccount account = service.createAccount(accountHolderName, pin);
        model.addAttribute("account", account);
        return "accountCreated";
    }

    @GetMapping("/deposit")
    public String depositForm() {
        return "deposit";
    }

    @PostMapping("/deposit")
    public String deposit(@RequestParam String accountNumber, @RequestParam double amount, @RequestParam int pin, Model model) {
        BankAccount account = service.getAccount(accountNumber).orElseThrow();
        if (service.validatePin(accountNumber, pin)) {
            service.deposit(accountNumber, amount);
            model.addAttribute("account", account);
            return "transactionSuccess";
        } else {
            model.addAttribute("error", "Invalid PIN");
            return "invalidPin";
        }
    }

    @GetMapping("/withdraw")
    public String withdrawForm() {
        return "withdraw";
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam String accountNumber, @RequestParam double amount, @RequestParam int pin, Model model) {
        BankAccount account = service.getAccount(accountNumber).orElseThrow(() -> new RuntimeException("Account not found"));
        String pinStr = String.valueOf(pin); // Convert the PIN to a string
        if (passwordEncoder.matches(pinStr, account.getPin())) {
            try {
                service.withdraw(accountNumber, amount);
                model.addAttribute("account", account);
                return "transactionSuccess";
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", e.getMessage());
                return "error";
            }
        } else {
            model.addAttribute("error", "Invalid PIN");
            return "invalidPin";
        }
    }

    @GetMapping("/balance")
    public String balanceForm() {
        return "balance";
    }

    @PostMapping("/balance")
    public String checkBalance(@RequestParam String accountNumber, @RequestParam int pin, Model model) {
        BankAccount account = service.getAccount(accountNumber).orElseThrow(() -> new RuntimeException("Account not found"));
        String pinStr = String.valueOf(pin); // Convert the PIN to a string
        if (passwordEncoder.matches(pinStr, account.getPin())) {
            model.addAttribute("balance", account.getBalance());
            return "balanceDisplay";
        } else {
            model.addAttribute("error", "Invalid PIN");
            return "invalidPin";
        }
    }

    @GetMapping("/view")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public String viewAllAccounts(Model model) {
        List<BankAccount> accounts = service.viewAllAccounts();
        model.addAttribute("accounts", accounts);
        return "viewAccounts";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

}

