package ma.java.springtransactiondemo.config;


import ma.java.springtransactiondemo.entities.Account;
import ma.java.springtransactiondemo.repositories.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component // <1>
public class DatabaseLoader implements CommandLineRunner { // <2>

    private final AccountRepository accountRepository;

    public DatabaseLoader(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    @Override
    public void run(String... strings) throws Exception { // <4>
        List<Account> accountList = Arrays.asList(new Account("acc1", 1000.01), new Account("acc2", 2000.01));
        this.accountRepository.saveAll(accountList);
    }
}
