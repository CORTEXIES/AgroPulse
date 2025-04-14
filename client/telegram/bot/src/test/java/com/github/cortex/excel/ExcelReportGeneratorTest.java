package com.github.cortex.excel;

import org.junit.jupiter.api.Test;
import com.github.cortex.exception.excel.ExcelTitleConfigParsingException;
import com.github.cortex.classification.server_dto.MessageClassification;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ExcelReportGeneratorTest {

    @Test
    void should_GenerateExcelFile_WithValidData() {

        List<MessageClassification> classifiedMessages = List.of(
                new MessageClassification(
                        LocalDateTime.now(),
                        "Dep1",
                        "Op1",
                        "Plant2",
                        100,
                        150,
                        200,
                        250
                    ),
                new MessageClassification(
                        LocalDateTime.now(),
                        "Dep2",
                        "Op2",
                        "Plant2",
                        100,
                        150,
                        200,
                        250
                )
        );
        String relativePath = "../../../reports/";
        String filePrefix = "Отчёт_за_";
        String columnsConfig = "{ \"0\": \"Дата\", \"1\": \"Подразделения\" }";
        String dateTimePattern = "dd_MM_yyyy";

        ExcelReportGenerator generator = new ExcelReportGenerator(relativePath, filePrefix, columnsConfig, dateTimePattern);
        File result = generator.generate(classifiedMessages);

        assertNotNull(result);
        assertTrue(result.exists());
        assertTrue(result.getName().endsWith(".xlsx"));
    }

    @Test
    void should_ParseTitleConfiguration_WithValidData() {
       	String columnsConfig = "{ \"0\": \"Дата\", \"1\": \"Подразделения\" }";
        Map<Integer, String> result = ExcelTitleConfigParser.getOrderToTitle(columnsConfig);

        assertEquals("Дата", result.get(0));
        assertEquals("Подразделения", result.get(1));
    }

    @Test
    void should_ThrowException_ForInvalidTitleConfig() {
        String invalidColumnsConfig = "FAILED";
        assertThrows(ExcelTitleConfigParsingException.class, () ->
                ExcelTitleConfigParser.getOrderToTitle(invalidColumnsConfig));
    }
}