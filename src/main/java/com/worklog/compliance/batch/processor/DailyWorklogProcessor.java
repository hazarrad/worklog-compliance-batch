package com.worklog.compliance.batch.processor;

import com.worklog.compliance.batch.config.WorklogProperties;
import com.worklog.compliance.batch.model.DailyWorklogAggregate;
import com.worklog.compliance.batch.model.DailyWorklogStatus;
import com.worklog.compliance.batch.model.DailyWorklogSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@StepScope
@RequiredArgsConstructor
public class DailyWorklogProcessor implements ItemProcessor<DailyWorklogAggregate, DailyWorklogSummary> {

    private final WorklogProperties properties;

    @Override
    public DailyWorklogSummary process(DailyWorklogAggregate item) {

        BigDecimal expected = properties.expectedHours().getOrDefault(item.workDate().getDayOfWeek(), BigDecimal.ZERO);
        BigDecimal actual = item.actualHours();
        BigDecimal difference = actual.subtract(expected);

        DailyWorklogStatus status = switch (difference.signum()) {
            case 0 -> DailyWorklogStatus.COMPLETE;
            case -1 -> DailyWorklogStatus.INCOMPLETE;
            default -> DailyWorklogStatus.OVER;
        };

        return new DailyWorklogSummary(item.personName(), item.workDate(), expected, actual, difference, status);
    }
}