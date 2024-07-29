package com.example.BankingApplication.service;

import com.example.BankingApplication.entity.User;

import java.util.List;

public interface UserService {

    User save(User user);

    User findByUsername(String username);

    List<User> findAll();
}
