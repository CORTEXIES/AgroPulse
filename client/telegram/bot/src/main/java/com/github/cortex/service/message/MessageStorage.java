package com.github.cortex.service.message;

import com.github.cortex.messaging.dto.UserMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class MessageStorage {

    private final List<UserMessage> messages = new CopyOnWriteArrayList<>();

    public void add(UserMessage message) {
        messages.add(message);
    }

    public List<UserMessage> getAndClear() {
        List<UserMessage> copy = new ArrayList<>(messages);
        messages.clear();
        return copy;
    }

    public boolean isEmpty() {
        return messages.isEmpty();
    }
}