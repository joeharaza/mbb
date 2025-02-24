package org.mbb.assessment.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @author Joe Haraza
 * @since 21/2/2025
 */

@Entity
@Table(name = "transactions")
@Getter
@Setter
@ToString
public class Transaction extends MasterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accountNumber;
    private Double trxAmount;
    private String description;
    private LocalDate trxDate;
    private LocalTime trxTime;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
