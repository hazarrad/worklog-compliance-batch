package com.worklog.compliance.batch.processor;

import com.worklog.compliance.batch.config.WorklogProperties;
import com.worklog.compliance.batch.model.DailyWorklogAggregate;
import com.worklog.compliance.batch.model.DailyWorklogStatus;
import com.worklog.compliance.batch.model.DailyWorklogSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DailyWorklogProcessorTest {

    private DailyWorklogProcessor processor;

    @BeforeEach
    void setUp() {

        WorklogProperties properties = new WorklogProperties(Map.of(DayOfWeek.MONDAY, new BigDecimal("9"), DayOfWeek.TUESDAY, new BigDecimal("9"), DayOfWeek.WEDNESDAY, new BigDecimal("9"), DayOfWeek.THURSDAY, new BigDecimal("9"), DayOfWeek.FRIDAY, new BigDecimal("7")));
        processor = new DailyWorklogProcessor(properties);
    }

    @Test
    void shouldMarkMondayAsCompleteWhenEmployeeWorkedNineHours() {

        DailyWorklogAggregate aggregate = new DailyWorklogAggregate("Hassan", LocalDate.of(2026, 9, 14), // Monday
                new BigDecimal("9"));

        DailyWorklogSummary result = processor.process(aggregate);

        assertEquals(new BigDecimal("9"), result.expectedHours());
        assertEquals(new BigDecimal("9"), result.actualHours());
        assertEquals(new BigDecimal("0"), result.differenceHours());
        assertEquals(DailyWorklogStatus.COMPLETE, result.status());
    }

    @Test
    void shouldMarkFridayAsCompleteWhenEmployeeWorkedSevenHours() {

        DailyWorklogAggregate aggregate = new DailyWorklogAggregate("Hassan", LocalDate.of(2026, 9, 18), // Friday
                new BigDecimal("7"));
        DailyWorklogSummary result = processor.process(aggregate);

        assertEquals(new BigDecimal("7"), result.expectedHours());
        assertEquals(DailyWorklogStatus.COMPLETE, result.status());
    }

    @Test
    void shouldMarkThursdayAsIncompleteWhenEmployeeWorkedLessThanNineHours() {

        DailyWorklogAggregate aggregate = new DailyWorklogAggregate("Hassan", LocalDate.of(2026, 9, 17), // Thursday
                new BigDecimal("8"));
        DailyWorklogSummary result = processor.process(aggregate);

        assertEquals(new BigDecimal("9"), result.expectedHours());
        assertEquals(new BigDecimal("-1"), result.differenceHours());
        assertEquals(DailyWorklogStatus.INCOMPLETE, result.status());
    }

    @Test
    void shouldMarkFridayAsIncompleteWhenEmployeeWorkedLessThanSevenHours() {

        DailyWorklogAggregate aggregate = new DailyWorklogAggregate("Hassan", LocalDate.of(2026, 9, 18), // Friday
                new BigDecimal("6"));

        DailyWorklogSummary result = processor.process(aggregate);

        assertEquals(new BigDecimal("7"), result.expectedHours());
        assertEquals(new BigDecimal("-1"), result.differenceHours());
        assertEquals(DailyWorklogStatus.INCOMPLETE, result.status());
    }

    @Test
    void shouldMarkEmployeeAsOverWhenWorkedMoreThanExpected() {

        DailyWorklogAggregate aggregate = new DailyWorklogAggregate("Hassan", LocalDate.of(2026, 9, 14), new BigDecimal("10"));
        DailyWorklogSummary result = processor.process(aggregate);

        assertEquals(DailyWorklogStatus.OVER, result.status());
        assertEquals(new BigDecimal("1"), result.differenceHours());
    }
}
