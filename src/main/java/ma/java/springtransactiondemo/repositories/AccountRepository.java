package ma.java.springtransactiondemo.repositories;

import ma.java.springtransactiondemo.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Modifying
    @Query("update Account set balance = balance + :amount where id = :id")
    void deposit(@Param("id") Long id, @Param("amount") Double amount);

    @Modifying
    @Query("update Account set balance = balance - :amount where id = :id")
    void withdraw(@Param("id") Long id, @Param("amount") Double amount);
}
