package com.github.cortex.service.message.command.impl;

import com.github.cortex.service.message.command.Command;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;

@Component
public class GetReportCommand extends Command {

    public GetReportCommand(@Value("${bot.command.get_report}") String command) {
        super(command);
    }

    @Override
    public BotApiMethod<?> execute() {
        return null;
    }
}