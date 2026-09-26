package com.worklog.compliance.batch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "worklog")
public record WorklogProperties(
        BigDecimal expectedHoursPerDay
) {
}