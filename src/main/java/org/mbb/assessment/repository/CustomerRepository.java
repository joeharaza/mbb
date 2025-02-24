package org.mbb.assessment.repository;

import org.mbb.assessment.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Joe Haraza
 * @since 23/2/2025
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
