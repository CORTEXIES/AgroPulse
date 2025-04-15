package com.github.cortex.excel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.cortex.exception.excel.ExcelTitleConfigParsingException;

import java.util.Map;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ExcelTitleConfigParser {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static Map<Integer, String> getOrderToTitle(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>(){});
        } catch (JsonProcessingException ex) {
            log.error("Title configuration parsing error for .xlsx file. Config: {}", json);
            throw new ExcelTitleConfigParsingException("Config: " + json);
        }
    }
}