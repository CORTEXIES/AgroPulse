package com.github.cortex.message;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class MessageBuffer<T> {

    private final Queue<T> messageQueue = new ConcurrentLinkedQueue<>();

    public void add(T message) {
        messageQueue.add(message);
    }

    public void addAll(List<T> messages) {
        messageQueue.addAll(messages);
    }

    public List<T> getAndClear() {
        List<T> result = new ArrayList<>();
        T message;
        while ((message = messageQueue.poll()) != null) {
            result.add(message);
        }
        return result;
    }

    public boolean isEmpty() {
        return messageQueue.isEmpty();
    }
}