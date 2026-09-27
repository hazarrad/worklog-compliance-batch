package com.worklog.compliance.batch.reader;

import com.worklog.compliance.batch.model.DailyWorklogAggregate;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.time.LocalDate;

@Configuration
public class DailyWorklogAggregateReader {

    @Bean
    @StepScope
    public JdbcCursorItemReader<DailyWorklogAggregate> dailyWorklogReader(DataSource dataSource, @Value("#{jobExecutionContext['importId']}") Long importId) {

        return new JdbcCursorItemReaderBuilder<DailyWorklogAggregate>().name("dailyWorklogReader").dataSource(dataSource).sql("""
                SELECT
                    person_name,
                    work_date,
                    SUM(hours) AS actual_hours
                FROM worklog_entry
                WHERE import_id = ?
                GROUP BY person_name, work_date
                ORDER BY person_name, work_date
                """).preparedStatementSetter(ps -> ps.setLong(1, importId))
                .rowMapper((rs, rowNum) -> new DailyWorklogAggregate(rs.getString("person_name"), rs.getObject("work_date", LocalDate.class), rs.getBigDecimal("actual_hours")))
                .build();
    }
}