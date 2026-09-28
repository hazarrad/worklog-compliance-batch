-- Worklog Compliance Batch
-- Database schema

CREATE TABLE worklog_import (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    status VARCHAR(30) NOT NULL
);

CREATE TABLE worklog_entry (
    id BIGSERIAL PRIMARY KEY,
    import_id BIGINT NOT NULL,
    person_name VARCHAR(255) NOT NULL,
    work_date DATE NOT NULL,
    hours DECIMAL(5,2) NOT NULL,

    CONSTRAINT fk_worklog_entry_import
        FOREIGN KEY (import_id)
        REFERENCES worklog_import(id)
);

CREATE INDEX idx_worklog_entry_import
    ON worklog_entry(import_id);

CREATE TABLE daily_worklog_summary (
    id BIGSERIAL PRIMARY KEY,
    import_id BIGINT NOT NULL,
    person_name VARCHAR(255) NOT NULL,
    work_date DATE NOT NULL,
    expected_hours DECIMAL(5,2) NOT NULL,
    actual_hours DECIMAL(5,2) NOT NULL,
    difference_hours DECIMAL(5,2) NOT NULL,
    status VARCHAR(30) NOT NULL,

    CONSTRAINT fk_daily_summary_import
        FOREIGN KEY (import_id)
        REFERENCES worklog_import(id),

    CONSTRAINT uk_daily_summary_person_date
        UNIQUE (person_name, work_date)
);