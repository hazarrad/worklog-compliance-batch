package com.worklog.compliance.batch.writer;

import com.worklog.compliance.batch.model.WorklogEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@StepScope
@RequiredArgsConstructor
public class WorklogEntryWriter implements ItemWriter<WorklogEntry> {

    private final JdbcTemplate jdbcTemplate;

    @Value("#{jobExecutionContext['importId']}")
    private Long importId;

    @Override
    public void write(Chunk<? extends WorklogEntry> chunk) {

        jdbcTemplate.batchUpdate("""
                INSERT INTO worklog_entry (
                    import_id,
                    person_name,
                    work_date,
                    hours
                )
                VALUES (?, ?, ?, ?)
                """, chunk.getItems(), chunk.size(), (ps, item) -> {
            ps.setLong(1, importId);
            ps.setString(2, item.personName());
            ps.setObject(3, item.workDate());
            ps.setBigDecimal(4, item.hours());
        });
    }
}