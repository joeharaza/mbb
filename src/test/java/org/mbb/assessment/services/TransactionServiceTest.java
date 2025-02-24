package org.mbb.assessment.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mbb.assessment.dto.TransactionDTO;
import org.mbb.assessment.entity.Transaction;
import org.mbb.assessment.repository.CustomerRepository;
import org.mbb.assessment.repository.TransactionRepository;
import org.mbb.assessment.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.net.URL;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * @author Joe Haraza
 * @since 24/2/2025
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private CustomerRepository customerRepository;

    @Test
    public void testProcessFilesInDirectory() {
//        URL resource = getClass().getClassLoader().getResource("datasource");
//        if (resource != null) {
//            File directory = new File(resource.getFile());
//            transactionService.processFilesInDirectory(directory.getAbsolutePath());
//        }

        // Mock the behavior of the repositories if needed
        // For example, when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        // Call the method to test
        transactionService.processFilesInDirectory("path/to/directory");

        // Verify interactions or assert results
        // For example, verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    public void testUpdateTransaction() {
        Transaction transaction = new Transaction();
        transaction.setDescription("Old Description");

        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setDescription("New Description");

        boolean result = transactionService.updateTransaction(1L, transactionDTO);

        assertTrue(result);
        assertEquals("New Description", transaction.getDescription());
    }

    @Test
    public void testUpdateTransactionNotFound() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.empty());

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setDescription("New Description");

        boolean result = transactionService.updateTransaction(1L, transactionDTO);

        assertFalse(result);
    }
}
