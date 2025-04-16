package com.github.cortex.agro.service;

import com.github.cortex.agro.dto.AgroMessage;
import com.github.cortex.agro.dto.Agronomist;
import com.github.cortex.message.MessageBuffer;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Service
public class AgroMessageFactory {

    private final MessageBuffer<AgroMessage> messageBuffer;

    @Autowired
    public AgroMessageFactory(MessageBuffer<AgroMessage> messageBuffer) {
        this.messageBuffer = messageBuffer;
    }

    public void createAndRecord(Message msg) {
        AgroMessage agroMessage = create(msg);
        messageBuffer.add(agroMessage);
    }

    private AgroMessage create(Message msg) {
        User consumer = msg.getFrom();
        String fullName = consumer.getFirstName();
        if (consumer.getLastName() != "null") {
        	fullName = String.format("%s %s", consumer.getFirstName(), consumer.getLastName());
        }
        Agronomist agronomist = new Agronomist(fullName, consumer.getId().toString());
        return new AgroMessage(agronomist, msg.getText());
    }
}