package com.github.cortex.command.impl;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;

import com.github.cortex.command.InternalCommand;

@Component
public class NoNewMessagesCommand extends InternalCommand {

    @Value("${bot.command.title.no_new_messages}")
    private String title;

    @Override
    public PartialBotApiMethod<?> execute(Message msg) {
        return SendMessage.builder()
                .chatId(msg.getChatId())
                .text(title)
                .build();
    }
}