package com.worklog.compliance.batch.config;

import com.worklog.compliance.batch.exception.InvalidWorklogRowException;
import com.worklog.compliance.batch.listener.WorklogImportStepListener;
import com.worklog.compliance.batch.listener.WorklogJobExecutionListener;
import com.worklog.compliance.batch.listener.WorklogSkipListener;
import com.worklog.compliance.batch.model.DailyWorklogAggregate;
import com.worklog.compliance.batch.model.DailyWorklogSummary;
import com.worklog.compliance.batch.model.WorklogEntry;
import com.worklog.compliance.batch.processor.DailyWorklogProcessor;
import com.worklog.compliance.batch.tasklet.CreateImportTasklet;
import com.worklog.compliance.batch.writer.DailyWorklogSummaryWriter;
import com.worklog.compliance.batch.writer.WorklogEntryWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemStreamReader;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class WorklogJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job worklogImportJob(Step createImportStep, Step importWorklogsStep, Step dailySummaryStep, WorklogJobExecutionListener listener) {
        return new JobBuilder("worklogImportJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(createImportStep)
                .next(importWorklogsStep)
                .next(dailySummaryStep)
                .build();
    }

    @Bean
    public Step createImportStep(CreateImportTasklet tasklet) {
        return new StepBuilder("createImportStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }

    @Bean
    public Step importWorklogsStep(ItemStreamReader<WorklogEntry> worklogReader, WorklogEntryWriter worklogWriter, WorklogSkipListener worklogSkipListener, WorklogImportStepListener worklogImportStepListener) {
        return new StepBuilder("importWorklogsStep", jobRepository)
                .<WorklogEntry, WorklogEntry>chunk(10)
                .transactionManager(transactionManager)
                .reader(worklogReader)
                .writer(worklogWriter)
                .faultTolerant()
                .skip(InvalidWorklogRowException.class)
                .skipLimit(1000)
                .listener(worklogSkipListener)
                .listener(worklogImportStepListener)
                .build();
    }

    @Bean
    public Step dailySummaryStep(JdbcCursorItemReader<DailyWorklogAggregate> reader, DailyWorklogProcessor processor, DailyWorklogSummaryWriter writer) {
        return new StepBuilder("dailySummaryStep", jobRepository)
                .<DailyWorklogAggregate, DailyWorklogSummary>chunk(10)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

}
