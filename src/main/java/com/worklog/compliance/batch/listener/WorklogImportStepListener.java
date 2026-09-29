package com.worklog.compliance.batch.listener;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

@Component
public class WorklogImportStepListener implements StepExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(WorklogImportStepListener.class);

    @Override
    public @Nullable ExitStatus afterStep(StepExecution stepExecution) {
        log.info(
                "Worklog import step completed: read={}, written={}, skipped={}, commits={}",
                stepExecution.getReadCount(),
                stepExecution.getWriteCount(),
                stepExecution.getSkipCount(),
                stepExecution.getCommitCount()
        );
        return StepExecutionListener.super.afterStep(stepExecution);
    }
}