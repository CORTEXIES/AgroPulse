package com.github.cortex.excel;

import java.io.File;
import java.util.List;
import java.util.Optional;

import com.github.cortex.classification.ClassifiedMessagePair;
import com.github.cortex.classification.dto.MessageClassification;
import com.github.cortex.classification.utils.MessageClassificationFetcher;
import com.github.cortex.classification.utils.MessageClassificationStatusUpdater;
import com.github.cortex.database.dto.classifcation.MessageClassificationEntity;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ExcelReportService {

    private final ExcelReportGenerator excelReportGenerator;
    private final MessageClassificationFetcher messageClassificationFetcher;
    private final MessageClassificationStatusUpdater statusUpdater;

    @Autowired
    public ExcelReportService(
            ExcelReportGenerator excelReportGenerator,
            MessageClassificationFetcher messageClassificationFetcher,
            MessageClassificationStatusUpdater statusUpdater
    ) {
        this.excelReportGenerator = excelReportGenerator;
        this.messageClassificationFetcher = messageClassificationFetcher;
        this.statusUpdater = statusUpdater;
    }

    public Optional<File> createReport() {

        List<ClassifiedMessagePair> classifiedMessagePairs = messageClassificationFetcher.fetchNewMessages();
        List<MessageClassification> classifiedMessages = classifiedMessagePairs.stream().
                map(ClassifiedMessagePair::dto).toList();

        if (classifiedMessages.isEmpty()) return Optional.empty();
        File excelReport = excelReportGenerator.generate(classifiedMessages);

        List<MessageClassificationEntity> entities = classifiedMessagePairs.stream()
                .map(ClassifiedMessagePair::entity)
                .toList();
        statusUpdater.markMessagesAsProcessed(entities);

        return Optional.of(excelReport);
    }
}