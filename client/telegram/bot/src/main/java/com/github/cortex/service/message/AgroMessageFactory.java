package com.github.cortex.service.message;

import com.github.cortex.messaging.dto.AgroMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Service
public class UserMessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public UserMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void extractAndRecord(Message msg) {
        AgroMessage agroMessage = createUserMessage(msg);
        messageRepository.add(agroMessage);
    }

    private AgroMessage createUserMessage(Message msg) {
        User consumer = msg.getFrom();
        String sender = consumer.getFirstName();
        if (consumer.getLastName() != "null") {
        	sender = String.format("%s %s", consumer.getFirstName(), consumer.getLastName());
        }
        return new AgroMessage(sender, consumer.getId().toString(), msg.getText());
    }
}