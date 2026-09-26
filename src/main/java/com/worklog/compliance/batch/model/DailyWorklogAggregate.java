package com.worklog.compliance.batch.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyWorklogAggregate(
        String personName,
        LocalDate workDate,
        BigDecimal actualHours
) {
}