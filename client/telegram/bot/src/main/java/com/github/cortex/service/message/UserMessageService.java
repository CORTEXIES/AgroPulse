package com.github.cortex.service.message;

import com.github.cortex.messaging.dto.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Service
public class UserMessageService {

    private final MessageStorage messageStorage;

    @Autowired
    public UserMessageService(MessageStorage messageStorage) {
        this.messageStorage = messageStorage;
    }

    public void extractAndRecord(Message msg) {
        UserMessage userMessage = createUserMessage(msg);
        messageStorage.add(userMessage);
    }

    private UserMessage createUserMessage(Message msg) {
        User consumer = msg.getFrom();
        String sender = consumer.getFirstName();
        if (consumer.getLastName() != "null") {
        	sender = String.format("%s %s", consumer.getFirstName(), consumer.getLastName());
        }
        return new UserMessage(sender, consumer.getId().toString(), msg.getText());
    }
}