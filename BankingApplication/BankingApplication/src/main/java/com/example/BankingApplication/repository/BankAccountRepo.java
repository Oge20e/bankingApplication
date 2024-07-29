package com.example.BankingApplication.repository;

import com.example.BankingApplication.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepo extends JpaRepository<BankAccount, String> {

    Optional<BankAccount> findByAccountNumber(String accountNumber);

}
