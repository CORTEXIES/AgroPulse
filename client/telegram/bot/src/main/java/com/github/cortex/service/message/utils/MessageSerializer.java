package com.github.cortex.service.message.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.cortex.messaging.dto.UserMessage;
import com.github.cortex.exception.SerializeMessageException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class MessageSerializer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MessageSerializer() {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public String serialize(List<UserMessage> messages) {
        try {
            return objectMapper.writeValueAsString(messages);
        } catch (JsonProcessingException e) {
            log.error("Error serializing messages form consumers. Messages: {}", messages.toString(), e);
            throw new SerializeMessageException("Messages: " + messages);
        }
    }
}