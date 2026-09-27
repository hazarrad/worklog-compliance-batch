package com.worklog.compliance.batch.reader;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.worklog.compliance.batch.exception.InvalidWorklogRowException;
import com.worklog.compliance.batch.model.WorklogEntry;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.ItemStreamReader;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class WorklogJsonItemReader implements ItemStreamReader<WorklogEntry> {

    private final JsonParser parser;

    private Map<String, Integer> columnIndexes;
    private boolean initialized;
    private boolean finished;
    private int rowNumber;

    public WorklogJsonItemReader(JsonParser parser) {
        this.parser = parser;
    }

    @Override
    public WorklogEntry read() throws Exception {

        if (finished) {
            return null;
        }

        if (!initialized) {
            initialize();
        }

        JsonToken token = parser.nextToken();

        if (token == JsonToken.END_ARRAY) {
            finished = true;
            return null;
        }

        if (token != JsonToken.START_ARRAY) {
            throw new IllegalStateException("Expected worklog row but found: " + token);
        }

        String[] row = readRow();

        rowNumber++;

        return toWorklogEntry(row);
    }

    private void initialize() throws IOException {

        JsonToken token;

        while ((token = parser.nextToken()) != null) {

            if (token == JsonToken.FIELD_NAME && "data".equals(parser.currentName())) {

                token = parser.nextToken();

                if (token != JsonToken.START_ARRAY) {
                    throw new IllegalStateException("Expected 'data' to contain an array");
                }

                // First array inside data is the header
                token = parser.nextToken();

                if (token != JsonToken.START_ARRAY) {
                    throw new IllegalStateException("Expected first data element to be the header");
                }

                String[] header = readRow();

                initializeColumns(header);

                initialized = true;

                return;
            }
        }

        throw new IllegalStateException("JSON does not contain a 'data' array");
    }

    private String[] readRow() throws IOException {

        String[] row = new String[100];

        int index = 0;

        JsonToken token;

        while ((token = parser.nextToken()) != JsonToken.END_ARRAY) {

            if (index >= row.length) {
                throw new IllegalStateException("JSON row contains more than " + row.length + " columns");
            }

            row[index++] = token == JsonToken.VALUE_NULL ? null : parser.getValueAsString();
        }

        String[] result = new String[index];

        System.arraycopy(row, 0, result, 0, index);

        return result;
    }

    private void initializeColumns(String[] header) {

        columnIndexes = new HashMap<>();

        for (int i = 0; i < header.length; i++) {

            if (header[i] != null) {
                columnIndexes.put(header[i].trim(), i);
            }
        }

        validateRequiredColumns();
    }

    private void validateRequiredColumns() {

        requireColumn("Name");
        requireColumn("Hours");
        requireColumn("Date");
    }

    private void requireColumn(String column) {

        if (!columnIndexes.containsKey(column)) {
            throw new IllegalStateException("Required column missing from JSON: " + column);
        }
    }

    private WorklogEntry toWorklogEntry(String[] row) {

        String personName = value(row, "Name");
        String hours = value(row, "Hours");
        String date = value(row, "Date");

        String rawData = String.join(" | ", row);

        if (personName == null || personName.isBlank()) {
            throw new InvalidWorklogRowException(rowNumber, rawData, "Person name is missing");
        }

        if (hours == null || hours.isBlank()) {
            throw new InvalidWorklogRowException(rowNumber, rawData, "Hours value is missing");
        }

        if (date == null || date.isBlank()) {
            throw new InvalidWorklogRowException(rowNumber, rawData, "Date value is missing");
        }

        return new WorklogEntry(personName.trim(), parseDate(date), parseHours(hours));
    }

    private String value(String[] row, String column) {

        Integer index = columnIndexes.get(column);

        if (index == null || index >= row.length) {
            return null;
        }

        return row[index];
    }

    private BigDecimal parseHours(String value) {

        try {
            String normalized = value.trim().replace("\u00A0", "").replace(" ", "").replace(",", ".");

            return new BigDecimal(normalized);

        } catch (NumberFormatException e) {
            throw new InvalidWorklogRowException(rowNumber, value, "Invalid hours value: [" + value + "]");
        }
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value.trim());
        } catch (Exception e) {
            throw new InvalidWorklogRowException(rowNumber, value, "Invalid date value: [" + value + "]");
        }
    }

    @Override
    public void open(ExecutionContext executionContext) {
        // Restart handling will be implemented once
        // the input resource strategy is finalized.
    }

    @Override
    public void update(ExecutionContext executionContext) {
    }

    @Override
    public void close() {

        try {
            parser.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}