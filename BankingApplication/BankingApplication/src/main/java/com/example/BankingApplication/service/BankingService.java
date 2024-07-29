package com.example.BankingApplication.service;

import com.example.BankingApplication.entity.BankAccount;

import java.util.List;
import java.util.Optional;

public interface BankingService {

    BankAccount createAccount(String accountHolderName, int pin);

    List<BankAccount> getAllAccounts();

    Optional<BankAccount> getAccount(String accountNumber);

    BankAccount deposit(String accountNumber, double amount);

    BankAccount withdraw(String accountNumber, double amount);

    List<BankAccount> viewAllAccounts();

    boolean validatePin(String accountNumber, int inputPin);

}
