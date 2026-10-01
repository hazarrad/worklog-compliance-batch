package com.worklog.compliance.batch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "batch")
public record BatchProperties(
        int chunksize,
        int skipLimit,
        int corepool,
        int maxpool,
        int queuelimit
) {
}