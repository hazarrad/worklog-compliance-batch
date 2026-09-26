package com.worklog.compliance.batch.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyWorklogSummary(
        String personName,
        LocalDate workDate,
        BigDecimal expectedHours,
        BigDecimal actualHours,
        BigDecimal differenceHours,
        DailyWorklogStatus status
) {
}