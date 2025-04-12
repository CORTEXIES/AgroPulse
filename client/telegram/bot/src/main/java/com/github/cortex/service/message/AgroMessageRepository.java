package com.github.cortex.service.message;

import com.github.cortex.messaging.dto.AgroMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class MessageRepository {

    private final List<AgroMessage> messages = new CopyOnWriteArrayList<>();

    public void add(AgroMessage message) {
        messages.add(message);
    }

    public List<AgroMessage> getAndClear() {
        List<AgroMessage> copy = new ArrayList<>(messages);
        messages.clear();
        return copy;
    }

    public boolean isEmpty() {
        return messages.isEmpty();
    }
}