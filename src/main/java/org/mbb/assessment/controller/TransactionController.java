package org.mbb.assessment.controller;

/**
 * @author Joe Haraza
 * @since 21/2/2025
 */

import org.mbb.assessment.dto.TransactionDTO;
import org.mbb.assessment.entity.Transaction;
import org.mbb.assessment.repository.TransactionRepository;
import org.mbb.assessment.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public Page<TransactionDTO> getAllTransactions(
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<Integer> size,
            @RequestParam Optional<String> sortBy,
            @RequestParam Optional<String> customerId,
            @RequestParam Optional<String> accountNumber,
            @RequestParam Optional<String> description) {

        Pageable pageable = PageRequest.of(
                page.orElse(0),
                size.orElse(10),
                Sort.by(sortBy.orElse("id")).ascending());

        Specification<Transaction> spec = Specification.where(null);
        return transactionService.getAllTransactions(pageable, customerId.orElse(null), accountNumber.orElse(null), description.orElse(null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable Long id, @RequestBody TransactionDTO newTransactionDTO) {
        boolean updated = transactionService.updateTransaction(id, newTransactionDTO);
        if (updated) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(304).build();
        }
    }
}
