package com.worklog.compliance.batch.writer;

import com.worklog.compliance.batch.model.DailyWorklogSummary;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class DailyWorklogSummaryWriter implements ItemWriter<DailyWorklogSummary> {

    private final JdbcTemplate jdbcTemplate;

    @Value("#{jobExecutionContext['importId']}")
    private Long importId;

    public DailyWorklogSummaryWriter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void write(Chunk<? extends DailyWorklogSummary> chunk) {

        jdbcTemplate.batchUpdate("""
                INSERT INTO daily_worklog_summary (
                    import_id,
                    person_name,
                    work_date,
                    expected_hours,
                    actual_hours,
                    difference_hours,
                    status
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (person_name, work_date)
                DO UPDATE SET
                    import_id = EXCLUDED.import_id,
                    expected_hours = EXCLUDED.expected_hours,
                    actual_hours = EXCLUDED.actual_hours,
                    difference_hours = EXCLUDED.difference_hours,
                    status = EXCLUDED.status
                """, chunk.getItems(), chunk.size(), (ps, item) -> {
            ps.setLong(1, importId);
            ps.setString(2, item.personName());
            ps.setObject(3, item.workDate());
            ps.setBigDecimal(4, item.expectedHours());
            ps.setBigDecimal(5, item.actualHours());
            ps.setBigDecimal(6, item.differenceHours());
            ps.setString(7, item.status().name());
        });
    }
}