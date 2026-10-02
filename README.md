# Worklog Compliance Batch

Spring Batch application designed to automate the processing of employee worklog data and generate daily compliance results.

## Overview

The application processes worklog data through a batch workflow, from data import to the generation of daily compliance results.

## Business Flow

```text
Worklog Source
      ↓
Data Import
      ↓
Worklog Processing
      ↓
Daily Aggregation
      ↓
Compliance Evaluation
      ↓
Daily Compliance Results
```

## Batch Workflow

The batch execution consists of the following steps:

* Create and track the import execution
* Import worklog data
* Aggregate worklog entries by employee and date
* Calculate daily compliance results
* Persist the results

## Result

The process generates a daily compliance summary containing:

* Employee
* Date
* Expected hours
* Actual hours
* Difference
* Compliance status

The results are persisted in PostgreSQL and can be used by reporting or management applications.

## Configuration

```yaml
batch:
  chunk-size: 200
  skip-limit: 50

worklog:
  expected-hours:
    MONDAY: 9
    TUESDAY: 9
    WEDNESDAY: 9
    THURSDAY: 9
    FRIDAY: 7
```

## Technology Stack

* Java 21
* Spring Boot
* Spring Batch
* Spring JDBC
* PostgreSQL
* Jackson
* Maven

## Database

The application uses PostgreSQL for:

* Import execution tracking
* Worklog data storage
* Daily compliance results
* Spring Batch execution metadata

## Input

The batch accepts worklog data from a JSON file.

The input file is provided through the `inputFile` job parameter.

```text
--inputFile=/path/to/worklogs.json
```

## Running the Application

Configure the PostgreSQL connection in `application.yml`, then run:

```bash
mvn clean install
```

```bash
mvn spring-boot:run
```

## Project Objective

Provide a reliable and automated batch process for transforming raw worklog data into structured daily compliance results.
