package com.github.cortex.agro.service;

import com.github.cortex.agro.dto.Agronomist;
import com.github.cortex.agro.dto.AgroMessage;
import com.github.cortex.message.buffer.AgroMessageBuffer;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.Optional;

@Service
public class AgroMessageFactory {

    private final AgroMessageBuffer agroMessageBuffer;

    @Autowired
    public AgroMessageFactory(AgroMessageBuffer agroMessageBuffer) {
        this.agroMessageBuffer = agroMessageBuffer;
    }

    public void createAndRecord(Message msg, Optional<String> photoUrl) {
        AgroMessage agroMessage = create(msg, photoUrl);
        agroMessageBuffer.add(agroMessage);
    }

    private AgroMessage create(Message msg, Optional<String> photoUrl) {
        User consumer = msg.getFrom();
        String fullName = consumer.getFirstName();
        if (consumer.getLastName() != null) {
        	fullName = " " + consumer.getLastName();
        }
        Agronomist agronomist = new Agronomist(fullName, consumer.getId().toString());

        if (photoUrl.isPresent()) {
            return new AgroMessage(agronomist, Optional.empty(), photoUrl);
        }
        return new AgroMessage(
                agronomist,
                Optional.of(msg.getText()),
                Optional.empty()
        );
    }
}