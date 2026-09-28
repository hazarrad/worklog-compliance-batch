package com.worklog.compliance.batch.repository;

import com.worklog.compliance.batch.model.ImportStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WorklogImportRepository {

    private final JdbcTemplate jdbcTemplate;

    public long create(String fileName) {

        String sql = """
                INSERT INTO worklog_import (
                    file_name,
                    started_at,
                    status
                )
                VALUES (?, CURRENT_TIMESTAMP, ?)
                RETURNING id
                """;

        return jdbcTemplate.queryForObject(sql, Long.class, fileName, ImportStatus.RUNNING.name());
    }

    public void markCompleted(long importId) {
        jdbcTemplate.update("""
                UPDATE worklog_import
                SET status = ?,
                    completed_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """, ImportStatus.COMPLETED.name(), importId);
    }

    public void markFailed(long importId) {
        jdbcTemplate.update("""
                UPDATE worklog_import
                SET status = ?,
                    completed_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """, ImportStatus.FAILED.name(), importId);
    }
}