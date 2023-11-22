package ma.java.springtransactiondemo.dtos;

import java.math.BigDecimal;

public record TransferRequestDTO(Long fromAccountId, Long toAccountId, Double amount) {
}
