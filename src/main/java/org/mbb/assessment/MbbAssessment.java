package org.mbb.assessment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Joe Haraza
 * @since 21/2/2025
 */

@SpringBootApplication
@EnableScheduling
public class MbbAssessment {
    public static void main(String[] args) {
        SpringApplication.run(MbbAssessment.class, args);
    }
}