package org.mbb.assessment.scheduler;

/**
 * @author Joe Haraza
 * @since 21/2/2025
 */
import lombok.extern.slf4j.Slf4j;
import org.mbb.assessment.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;

@Slf4j
@Component
public class FileReadScheduler {
    @Autowired
    private TransactionService transactionService;
    @Scheduled(cron = "0 0 0 * * ?") // Runs at 12 AM every day
    public void readFilesInDirectory() {
        log.info("Scheduler start");
        URL resource = getClass().getClassLoader().getResource("datasource");
        if (resource != null) {
            File directory = new File(resource.getFile());
            transactionService.processFilesInDirectory(directory.getAbsolutePath());
        } else {
            System.out.println("Directory not found!");
        }
    }
}