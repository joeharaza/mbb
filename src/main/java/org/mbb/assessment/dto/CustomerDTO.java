package org.mbb.assessment.dto;

import lombok.*;

/**
 * @author Joe Haraza
 * @since 21/2/2025
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {
    private Long id;
    private String name;
    private String email;
}
