package com.worklog.compliance.batch.writer;

import com.worklog.compliance.batch.model.DailyWorklogStatus;
import com.worklog.compliance.batch.model.DailyWorklogSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;

class DailyWorklogSummaryWriterTest {

    private JdbcTemplate jdbcTemplate;
    private DailyWorklogSummaryWriter writer;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        writer = new DailyWorklogSummaryWriter(jdbcTemplate);
        ReflectionTestUtils.setField(writer, "importId", 14L);
    }

    @Test
    void shouldWriteDailyWorklogSummary() {

        DailyWorklogSummary summary = new DailyWorklogSummary("Hassan Zarrad", LocalDate.of(2026, 9, 14), new BigDecimal("9.00"), new BigDecimal("9.00"), new BigDecimal("0.00"), DailyWorklogStatus.COMPLETE);
        Chunk<DailyWorklogSummary> chunk = new Chunk<>(List.of(summary));
        writer.write(chunk);

        verify(jdbcTemplate).batchUpdate(anyString(), anyList(), anyInt(), any());
    }

    @Test
    void shouldWriteMultipleDailyWorklogSummaries() {

        DailyWorklogSummary hassan = new DailyWorklogSummary("Hassan Zarrad", LocalDate.of(2026, 9, 14), new BigDecimal("9.00"), new BigDecimal("9.00"), new BigDecimal("0.00"), DailyWorklogStatus.COMPLETE);
        DailyWorklogSummary mario = new DailyWorklogSummary("Mario Baq", LocalDate.of(2026, 9, 14), new BigDecimal("9.00"), new BigDecimal("8.00"), new BigDecimal("-1.00"), DailyWorklogStatus.INCOMPLETE);
        Chunk<DailyWorklogSummary> chunk = new Chunk<>(List.of(hassan, mario));

        writer.write(chunk);

        verify(jdbcTemplate).batchUpdate(anyString(), eq(List.of(hassan, mario)), eq(2), any());
    }

    @Test
    void shouldUseConfiguredImportId() {

        DailyWorklogSummary summary = new DailyWorklogSummary("Hassan Zarrad", LocalDate.of(2026, 9, 14), new BigDecimal("9.00"), new BigDecimal("9.00"), new BigDecimal("0.00"), DailyWorklogStatus.COMPLETE);
        Chunk<DailyWorklogSummary> chunk = new Chunk<>(List.of(summary));

        writer.write(chunk);

        verify(jdbcTemplate).batchUpdate(anyString(), anyList(), eq(1), any());

        Object importId = ReflectionTestUtils.getField(writer, "importId");

        assertEquals(14L, importId);
    }
}

