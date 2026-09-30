package com.worklog.compliance.batch.reader;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.worklog.compliance.batch.model.WorklogEntry;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ItemStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Path;

@Configuration
public class WorklogJsonReaderConfig {

    @Bean
    @StepScope
    public ItemStreamReader<WorklogEntry> worklogReader(@Value("#{jobParameters['inputFile']}") String inputFile) throws IOException {

        JsonFactory jsonFactory = new JsonFactory();
        JsonParser parser = jsonFactory.createParser(Path.of(inputFile).toFile());
        return new WorklogJsonItemReader(parser);
    }
}