package com.github.cortex.agro;

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
        String sender = consumer.getFirstName();
        if (consumer.getLastName() != "null") {
        	sender = String.format("%s %s", consumer.getFirstName(), consumer.getLastName());
        }
        return new AgroMessage(sender, consumer.getId().toString(), msg.getText());
    }
}