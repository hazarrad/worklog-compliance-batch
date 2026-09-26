package com.worklog.compliance.batch.listener;

import com.worklog.compliance.batch.repository.WorklogImportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorklogJobExecutionListener implements JobExecutionListener {

    private final WorklogImportRepository importRepository;

    @Override
    public void afterJob(JobExecution jobExecution) {

        ExecutionContext context = jobExecution.getExecutionContext();

        if (!context.containsKey("importId")) {
            return;
        }

        long importId = context.getLong("importId");

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            importRepository.markCompleted(importId);
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            importRepository.markFailed(importId);
        }
    }
}