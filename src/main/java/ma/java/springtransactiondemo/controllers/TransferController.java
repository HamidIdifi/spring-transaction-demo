package ma.java.springtransactiondemo.controllers;

import ma.java.springtransactiondemo.dtos.TransferRequestDTO;
import ma.java.springtransactiondemo.services.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {
    TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequestDTO transferRequestDTO) {
        transferService.transfer(transferRequestDTO.fromAccountId(), transferRequestDTO.toAccountId(), transferRequestDTO.amount());
        return ResponseEntity.ok("Transfer successful");
    }

}
