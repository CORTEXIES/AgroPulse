package com.github.cortex.service.message;

import com.github.cortex.messaging.dto.AgroMessage;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Service
public class AgroMessageFactory {

    private final AgroMessageRepository agroMessageRepository;

    @Autowired
    public AgroMessageFactory(AgroMessageRepository agroMessageRepository) {
        this.agroMessageRepository = agroMessageRepository;
    }

    public void createAndRecord(Message msg) {
        AgroMessage agroMessage = create(msg);
        agroMessageRepository.add(agroMessage);
    }

    private AgroMessage create(Message msg) {
        User consumer = msg.getFrom();
        String sender = consumer.getFirstName();
        if (consumer.getLastName() != "null") {
        	sender = String.format("%s %s", consumer.getFirstName(), consumer.getLastName());
        }
        return new AgroMessage(sender, consumer.getId().toString(), msg.getText());
    }
}