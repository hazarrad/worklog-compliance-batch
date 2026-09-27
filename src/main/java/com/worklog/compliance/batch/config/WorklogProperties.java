package com.worklog.compliance.batch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.Map;

@ConfigurationProperties(prefix = "worklog")
public record WorklogProperties(
        Map<DayOfWeek, BigDecimal> expectedHours
) {
}