package com.example.BankingApplication.service.impl;

import com.example.BankingApplication.entity.BankAccount;
import com.example.BankingApplication.repository.BankAccountRepo;
import com.example.BankingApplication.service.BankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.*;

@Service
public class BankAccountServiceImpl implements BankingService {
    @Autowired
    private BankAccountRepo repository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    private Random random = new Random();


    public BankAccount createAccount(String accountHolderName, int pin) {
        String accountNumber = generateAccountNumber();
        String hashedPin = passwordEncoder.encode(String.valueOf(pin)); // Convert PIN to string before hashing
        BankAccount account = new BankAccount(accountNumber, accountHolderName, 0.00, hashedPin);
        return repository.save(account);
    }

    private int getPinFromUser() {
        Scanner scanner = new Scanner(System.in);
        int pin;
        while (true) {
            System.out.print("Enter your 4-digit PIN: ");
            pin = scanner.nextInt();
            if (String.valueOf(pin).length() == 4) {
                break;
            } else {
                System.out.println("Invalid PIN. Please enter a 4-digit PIN.");
            }
        }
        return pin;
    }

    private String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = String.valueOf(random.nextInt(1000000000) + 1000000000);
        } while (repository.existsById(accountNumber));
        return accountNumber;
    }


    public List<BankAccount> getAllAccounts() {
        return repository.findAll();
    }


    public Optional<BankAccount> getAccount(String accountNumber) {
        return repository.findByAccountNumber(accountNumber);
    }


    public BankAccount deposit(String accountNumber, double amount) {
        BankAccount account = repository.findById(accountNumber).orElseThrow();
        account.setBalance(account.getBalance() + amount);
        return repository.save(account);
    }


    public BankAccount withdraw(String accountNumber, double amount) {
        BankAccount account = repository.findById(accountNumber).orElseThrow();
        if (account.getBalance() >= amount) {
            account.setBalance(account.getBalance() - amount);
            return repository.save(account);
        } else {
            throw new IllegalArgumentException("Insufficient balance");
        }
    }

    //    public List<BankAccount> viewAllAccounts() {
//        return new ArrayList<>(accounts.values());
    public List<BankAccount> viewAllAccounts() {
        return repository.findAll();


    }

    @Override
    public boolean validatePin(String accountNumber, int inputPin) {
        Optional<BankAccount> optionalAccount = repository.findByAccountNumber(accountNumber);
        if (optionalAccount.isPresent()) {
            BankAccount account = optionalAccount.get();
            return passwordEncoder.matches(String.valueOf(inputPin), account.getPin()); // Convert input PIN to string before matching
        }
        return false;
    }

}
