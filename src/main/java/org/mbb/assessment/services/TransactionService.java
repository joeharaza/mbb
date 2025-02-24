package org.mbb.assessment.services;

/**
 * @author Joe Haraza
 * @since 21/2/2025
 */

import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.mbb.assessment.dto.CustomerDTO;
import org.mbb.assessment.dto.TransactionDTO;
import org.mbb.assessment.entity.Customer;
import org.mbb.assessment.entity.Transaction;
import org.mbb.assessment.repository.CustomerRepository;
import org.mbb.assessment.repository.TransactionRepository;
import org.mbb.assessment.specification.TransactionSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private CustomerRepository customerRepository;
    public void processFilesInDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

        if (files != null) {
            for (File file : files) {
                readFileAndInsertData(file);
            }
        }
    }


    private void readFileAndInsertData(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }
                String[] data = line.split("\\|");
                if (isValidData(data)) {
                    Transaction transaction = new Transaction();
                    transaction.setAccountNumber(data[0]);
                    transaction.setTrxAmount(Double.parseDouble(data[1]));
                    transaction.setDescription(data[2]);
                    transaction.setTrxDate(LocalDate.parse(data[3]));
                    transaction.setTrxTime(LocalTime.parse(data[4]));

                    Customer customer = customerRepository.findById(Long.parseLong(data[5])).orElse(null);
                    if (customer != null) {
                        transaction.setCustomer(customer);
                    }

                    transactionRepository.save(transaction);
                } else {
                    System.out.println("Invalid data: " + String.join(", ", data));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidData(String[] data) {
        boolean valid = true;

        if (!isNumeric(data[0])) {
            log.info("Invalid account number: " + data[0]);
            valid = false;
        }
        if (!isDouble(data[1])) {
            log.info("Invalid transaction amount: " + data[1]);
            valid = false;
        }
        if (!isValidDate(data[3])) {
            log.info("Invalid transaction date: " + data[3]);
            valid = false;
        }
        if (!isValidTime(data[4])) {
            log.info("Invalid transaction time: " + data[4]);
            valid = false;
        }
        if (!isNumeric(data[5])) {
            log.info("Invalid customer ID: " + data[5]);
            valid = false;
        }

        return valid;
    }

    private boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }

    private boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidDate(String str) {
        try {
            LocalDate.parse(str, DateTimeFormatter.ISO_LOCAL_DATE);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean isValidTime(String str) {
        try {
            LocalTime.parse(str, DateTimeFormatter.ISO_LOCAL_TIME);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public Page<TransactionDTO> getAllTransactions(Pageable pageable, String customerId, String accountNumber, String description) {
        Specification<Transaction> spec = Specification.where(null);

        if (customerId != null) {
            spec = spec.and(TransactionSpecification.hasCustomerId(customerId));
        }
        if (accountNumber != null) {
            spec = spec.and(TransactionSpecification.hasAccountNumber(accountNumber));
        }
        if (description != null) {
            spec = spec.and(TransactionSpecification.hasDescriptionContaining(description));
        }

        Page<Transaction> transactions = transactionRepository.findAll(spec, pageable);

        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(transactionDTOs, pageable, transactions.getTotalElements());

    }

    private TransactionDTO convertToDTO(Transaction transaction) {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setId(transaction.getId());
        transactionDTO.setAccountNumber(transaction.getAccountNumber());
        transactionDTO.setTrxAmount(transaction.getTrxAmount());
        transactionDTO.setDescription(transaction.getDescription());
        transactionDTO.setTrxDate(transaction.getTrxDate());
        transactionDTO.setTrxTime(transaction.getTrxTime());

        Customer customer = transaction.getCustomer();
        if (customer != null) {
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setId(customer.getId());
            customerDTO.setName(customer.getName());
            customerDTO.setEmail(customer.getEmail());
            transactionDTO.setCustomer(customerDTO);
        }
        return transactionDTO;
    }

    @Retry(name = "transactionService", fallbackMethod = "fallbackUpdateTransaction")
    public boolean updateTransaction(Long id, TransactionDTO newTransactionDTO) {
        Optional<Transaction> existingTransaction = transactionRepository.findById(id);
        if (existingTransaction.isPresent()) {
            Transaction transaction = existingTransaction.get();
            if (!transaction.getDescription().equals(newTransactionDTO.getDescription())) {
                transaction.setDescription(newTransactionDTO.getDescription());
                transactionRepository.save(transaction);
                return true;
            }
        }
        return false;
    }

    public boolean fallbackUpdateTransaction(Long id, TransactionDTO newTransactionDTO, Throwable t) {
        log.error("Failed to update transaction with id " + id, t);
        return false;
    }
}