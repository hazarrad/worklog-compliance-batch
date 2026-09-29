package com.worklog.compliance.batch.listener;

import com.worklog.compliance.batch.exception.InvalidWorklogRowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.stereotype.Component;

@Component
public class WorklogSkipListener implements SkipListener<Object, Object> {

    private static final Logger log = LoggerFactory.getLogger(WorklogSkipListener.class);

    @Override
    public void onSkipInRead(Throwable t) {

        if (t instanceof InvalidWorklogRowException ex) {
            log.warn("Skipping worklog row {}: {} | rawData={}", ex.getRowNumber(), ex.getMessage(), ex.getRawData());
            return;
        }
        log.warn("Skipping worklog during read: {}", t.getMessage(), t);
    }

    @Override
    public void onSkipInProcess(Object item, Throwable t) {
        log.warn("Skipping worklog during processing: {}", t.getMessage(), t);
    }

    @Override
    public void onSkipInWrite(Object item, Throwable t) {
        log.warn("Skipping worklog during writing: {}", t.getMessage(), t);
    }
}