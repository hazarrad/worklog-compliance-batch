package com.worklog.compliance.batch;

import com.worklog.compliance.batch.config.WorklogProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(WorklogProperties.class)
public class WorklogComplianceBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorklogComplianceBatchApplication.class, args);
	}

}
