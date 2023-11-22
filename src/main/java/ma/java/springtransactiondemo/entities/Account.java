package ma.java.springtransactiondemo.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double balance;

    // constructors, getters and setters

    public Account(String name, Double balance) {
        this.name = name;
        this.balance = balance;
    }

    public Account() {
    }

    public String getName() {
        return name;
    }

    public Double getBalance() {
        return balance;
    }
}
