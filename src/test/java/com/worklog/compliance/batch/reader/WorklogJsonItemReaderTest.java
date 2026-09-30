package com.worklog.compliance.batch.reader;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.worklog.compliance.batch.exception.InvalidWorklogRowException;
import com.worklog.compliance.batch.model.WorklogEntry;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class WorklogJsonItemReaderTest {

    private WorklogJsonItemReader createReader(String json) throws IOException {

        JsonParser parser = new JsonFactory().createParser(new StringReader(json));
        return new WorklogJsonItemReader(parser);
    }

    @Test
    void shouldReadValidWorklog() throws Exception {

        String json = """
                {
                  "data": [
                    ["Name", "Hours", "Date"],
                    ["Hassan", "7,00", "2026-09-14"]
                  ]
                }
                """;

        var reader = createReader(json);
        WorklogEntry result = reader.read();

        assertNotNull(result);
        assertEquals("Hassan", result.personName());
        assertEquals(LocalDate.of(2026, 9, 14), result.workDate());
        assertEquals(new BigDecimal("7.00"), result.hours());

        assertNull(reader.read());

        reader.close();
    }

    @Test
    void shouldNormalizeHoursWithSpaceBeforeComma() throws Exception {

        String json = """
                {
                  "data": [
                    ["Name", "Hours", "Date"],
                    ["Hassan", "7 ,00", "2026-09-14"]
                  ]
                }
                """;

        var reader = createReader(json);
        WorklogEntry result = reader.read();

        assertEquals(new BigDecimal("7.00"), result.hours());
        reader.close();
    }

    @Test
    void shouldNormalizeHoursWithSpaceAfterComma() throws Exception {

        String json = """
                {
                  "data": [
                    ["Name", "Hours", "Date"],
                    ["Hassan", "7, 00", "2026-09-14"]
                  ]
                }
                """;

        var reader = createReader(json);
        WorklogEntry result = reader.read();
        assertEquals(new BigDecimal("7.00"), result.hours());

        reader.close();
    }

    @Test
    void shouldThrowInvalidWorklogRowExceptionForInvalidHours() throws Exception {

        String json = """
                {
                  "data": [
                    ["Name", "Hours", "Date"],
                    ["Hassan", "abc", "2026-09-14"]
                  ]
                }
                """;

        var reader = createReader(json);
        InvalidWorklogRowException exception = assertThrows(InvalidWorklogRowException.class, reader::read);

        assertEquals(1, exception.getRowNumber());
        assertTrue(exception.getMessage().contains("Invalid hours value"));

        reader.close();
    }

    @Test
    void shouldThrowInvalidWorklogRowExceptionForInvalidDate() throws Exception {

        String json = """
                {
                  "data": [
                    ["Name", "Hours", "Date"],
                    ["Hassan", "7,00", "2026/09/14"]
                  ]
                }
                """;

        var reader = createReader(json);
        InvalidWorklogRowException exception = assertThrows(InvalidWorklogRowException.class, reader::read);

        assertEquals(1, exception.getRowNumber());
        assertTrue(exception.getMessage().contains("Invalid date value"));

        reader.close();
    }

    @Test
    void shouldThrowExceptionWhenNameIsMissing() throws Exception {

        String json = """
                {
                  "data": [
                    ["Name", "Hours", "Date"],
                    ["", "7,00", "2026-09-14"]
                  ]
                }
                """;

        var reader = createReader(json);
        InvalidWorklogRowException exception = assertThrows(InvalidWorklogRowException.class, reader::read);

        assertEquals(1, exception.getRowNumber());
        assertEquals("Person name is missing", exception.getMessage());

        reader.close();
    }

    @Test
    void shouldThrowExceptionWhenHoursIsMissing() throws Exception {

        String json = """
                {
                  "data": [
                    ["Name", "Hours", "Date"],
                    ["Hassan", "", "2026-09-14"]
                  ]
                }
                """;

        var reader = createReader(json);
        InvalidWorklogRowException exception = assertThrows(InvalidWorklogRowException.class, reader::read);

        assertEquals("Hours value is missing", exception.getMessage());

        reader.close();
    }

    @Test
    void shouldReadMultipleRows() throws Exception {

        String json = """
                {
                  "data": [
                    ["Name", "Hours", "Date"],
                    ["Hassan", "2,00", "2026-09-14"],
                    ["Hassan", "7,00", "2026-09-14"],
                    ["Mario", "9,00", "2026-09-14"]
                  ]
                }
                """;

        var reader = createReader(json);
        WorklogEntry first = reader.read();
        WorklogEntry second = reader.read();
        WorklogEntry third = reader.read();

        assertEquals("Hassan", first.personName());
        assertEquals(new BigDecimal("2.00"), first.hours());

        assertEquals("Hassan", second.personName());
        assertEquals(new BigDecimal("7.00"), second.hours());

        assertEquals("Mario", third.personName());
        assertEquals(new BigDecimal("9.00"), third.hours());

        assertNull(reader.read());

        reader.close();
    }
}