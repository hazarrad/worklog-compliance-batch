package com.worklog.compliance.batch.tasklet;

import com.worklog.compliance.batch.repository.WorklogImportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@StepScope
@RequiredArgsConstructor
public class CreateImportTasklet implements Tasklet {

    private final WorklogImportRepository importRepository;

    @Value("#{jobParameters['inputFile']}")
    private String inputFile;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

        if (inputFile == null || inputFile.isBlank()) {
            throw new IllegalArgumentException("Missing required job parameter: inputFile");
        }

        JobExecution jobExecution = chunkContext.getStepContext().getStepExecution().getJobExecution();
        long importId = importRepository.create(Path.of(inputFile).getFileName().toString());
        jobExecution.getExecutionContext().putLong("importId", importId);

        return RepeatStatus.FINISHED;
    }
}