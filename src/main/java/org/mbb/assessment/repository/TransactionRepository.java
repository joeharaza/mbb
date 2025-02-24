package org.mbb.assessment.repository;

/**
 * @author Joe Haraza
 * @since 21/2/2025
 */

import org.mbb.assessment.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
}