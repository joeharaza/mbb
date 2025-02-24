package org.mbb.assessment.specification;

/**
 * @author Joe Haraza
 * @since 21/2/2025
 */
import org.mbb.assessment.entity.Transaction;
import org.springframework.data.jpa.domain.Specification;

public class TransactionSpecification {

    public static Specification<Transaction> hasCustomerId(String customerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("customer").get("id"), customerId);
    }

    public static Specification<Transaction> hasAccountNumber(String accountNumber) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("accountNumber"), accountNumber);
    }

    public static Specification<Transaction> hasDescriptionContaining(String description) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("description"), "%" + description + "%");
    }
}
