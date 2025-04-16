package com.github.cortex.command.impl;

import com.github.cortex.command.Command;
import com.github.cortex.excel.ExcelReportService;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;

import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;

import java.io.File;
import java.util.Optional;

@Component
public class GetReportCommand extends Command {

    private final ExcelReportService excelReportService;
    private final NoNewMessagesCommand noNewMessagesCommand;

    @Autowired
    public GetReportCommand(
            @Value("${bot.command.get_report}") String command,
            ExcelReportService excelReportService,
            NoNewMessagesCommand noNewMessagesCommand
    ) {
        super(command);
        this.excelReportService = excelReportService;
        this.noNewMessagesCommand = noNewMessagesCommand;
    }

    @Override
    public PartialBotApiMethod<?> execute(Message msg) {

        Optional<File> reportOpt = excelReportService.createReport();
        if (reportOpt.isEmpty()) return noNewMessagesCommand.execute(msg);

        InputFile inputFile = new InputFile(reportOpt.get());
        return SendDocument.builder()
                .document(inputFile)
                .chatId(msg.getChatId())
                .build();
    }
}