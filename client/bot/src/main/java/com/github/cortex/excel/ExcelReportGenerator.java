package com.github.cortex.excel;

import java.io.File;
import java.time.DateTimeException;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import java.time.LocalDateTime;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import lombok.extern.log4j.Log4j2;
import com.github.cortex.classification.dto.MessageClassification;

@Log4j2
@Component
public class ExcelReportGenerator {

    private final DateTimeFormatter dateTimeFormatter;

    private final String relativePath;
    private final String filePrefix;
    private final String columnsConfig;
    private final String fallbackCellValue;

    public ExcelReportGenerator(
            @Value("${excel.relative_path}") String relativePath,
            @Value("${excel.file_prefix}") String filePrefix,
            @Value("${excel.columns_config}") String columnsConfig,
            @Value("${excel.date_time_pattern}") String pattern,
            @Value("${excel.cell.fallback_value}") String fallbackCellValue
    ) {
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        this.relativePath = relativePath;
        this.filePrefix = filePrefix;
        this.columnsConfig = columnsConfig;
        this.fallbackCellValue =fallbackCellValue;
    }

    public File generate(List<MessageClassification> messages) {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        CellStyle baseStyle = createBaseStyle(workbook);
        applyBorderStyle(baseStyle);

        CellStyle headerStyle = createHeaderStyle(workbook, baseStyle);
        applyBorderStyle(headerStyle);

        Map<Integer, String> orderToTitle = ExcelTitleConfigParser.getOrderToTitle(columnsConfig);

        createHeader(sheet, headerStyle, orderToTitle);
        insertDataIntoTable(messages, sheet, baseStyle);

        for (int i = 0; i < orderToTitle.size(); i++) {
            sheet.autoSizeColumn(i);
        }
        return createFile(workbook);
    }

    private CellStyle createBaseStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 14);

        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);

        return style;
    }

    private CellStyle createHeaderStyle(Workbook workbook, CellStyle baseStyle) {

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.cloneStyleFrom(baseStyle);

        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 16);

        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setWrapText(true);
        headerStyle.setFont(font);

        return headerStyle;
    }

    private void applyBorderStyle(CellStyle cellStyle) {
        cellStyle.setBorderBottom(BorderStyle.THICK);
        cellStyle.setBorderTop(BorderStyle.THICK);
        cellStyle.setBorderLeft(BorderStyle.THICK);
        cellStyle.setBorderRight(BorderStyle.THICK);
    }

    private void createHeader(Sheet sheet, CellStyle style, Map<Integer, String> orderToTitle) {
        Row header = sheet.createRow(0);
        header.setHeightInPoints(40);

        orderToTitle.forEach((colIndex, title) -> {
            Cell cell = header.createCell(colIndex);
            cell.setCellValue(title);
            cell.setCellStyle(style);
        });
    }

    private void insertDataIntoTable(List<MessageClassification> messages, Sheet sheet, CellStyle cellStyle) {
        int rowId = 1;
        for (MessageClassification msg : messages) {
            Row row = sheet.createRow(rowId++);
            fillRowWithData(row, msg, cellStyle);
        }
    }

    private void fillRowWithData(Row row, MessageClassification msg, CellStyle cellStyle) {
        Cell cell;
        cell = row.createCell(0);
        cell.setCellValue(formatDate(msg.getDate()));

        CellStyle dateStyle = createDateStyle(row.getSheet().getWorkbook(), cellStyle);
        cell.setCellStyle(dateStyle);

        cell = row.createCell(1);
        String cellValue = prettyValue(msg.getDepartment());
        cell.setCellValue(cellValue);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(2);
        cellValue = prettyValue(msg.getOperation());
        cell.setCellValue(cellValue);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(3);
        cellValue = prettyValue(msg.getPlant());
        cell.setCellValue(cellValue);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(4);
        cellValue = prettyValue(msg.getPerDay());
        cell.setCellValue(cellValue);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(5);
        cellValue = prettyValue(msg.getPerOperation());
        cell.setCellValue(cellValue);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(6);
        cellValue = prettyValue(msg.getGrosPerDay());
        cell.setCellValue(cellValue);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(7);
        cellValue = prettyValue(msg.getGrosPerOperation());
        cell.setCellValue(cellValue);
        cell.setCellStyle(cellStyle);
    }

    private String prettyValue(String value) {
        return (value == null || value.isEmpty()) ? fallbackCellValue : value;
    }

    private String prettyValue(Integer value) {
        return (value == null || value == -1) ? fallbackCellValue : value.toString();
    }

    private CellStyle createDateStyle(Workbook workbook, CellStyle baseCellStyle) {
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.cloneStyleFrom(baseCellStyle);

        short dateFormat = workbook.createDataFormat().getFormat("dd.MM.yyyy");
        dateStyle.setDataFormat(dateFormat);
        return dateStyle;
    }

    private String formatDate(LocalDateTime dateTime) {
        try {
        	return dateTime != null ? dateTime.format(dateTimeFormatter) : "Не указана";
        } catch (DateTimeException ex) {
            log.error("Error converting the date using date time formatter", ex);
            return dateTime.toString();
        }
    }

    private File createFile(Workbook workbook) {
        String path = createFilePathWithData();
        File excelFile = new File(path);

        try (FileOutputStream fos = new FileOutputStream(excelFile)) {
            workbook.write(fos);
        } catch (FileNotFoundException ex) {
            log.error("Incorrect path for creating an .xlsx file. Path: {}", path);
        } catch (IOException ex) {
	        log.error("Filed to create .xlsx file. Path: {}", path);
        }
        return excelFile;
    }

    private String createFilePathWithData() {
        LocalDateTime now = LocalDateTime.now();
        return relativePath + filePrefix + now.format(dateTimeFormatter) + ".xlsx";
    }
}