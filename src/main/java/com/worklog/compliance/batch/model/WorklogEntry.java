package com.worklog.compliance.batch.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WorklogEntry(
        String personName,
        LocalDate workDate,
        BigDecimal hours
) {
}
