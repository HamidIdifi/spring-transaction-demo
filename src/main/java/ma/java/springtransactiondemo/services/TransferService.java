package ma.java.springtransactiondemo.services;

import jakarta.transaction.Transactional;
import ma.java.springtransactiondemo.entities.Account;
import ma.java.springtransactiondemo.exceptions.AccountNotFoundException;
import ma.java.springtransactiondemo.exceptions.InsufficientFundsException;
import ma.java.springtransactiondemo.repositories.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransferService {

    private final AccountRepository accountRepository;

    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void transfer(Long fromAccountId, Long toAccountId, Double amount) {
        Account sourceAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Source account not found"));

        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Not enough funds in the source account.");
        }
        accountRepository.withdraw(fromAccountId, amount);
        accountRepository.deposit(toAccountId, amount);
    }
}
