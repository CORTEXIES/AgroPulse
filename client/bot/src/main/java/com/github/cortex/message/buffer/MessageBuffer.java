package com.github.cortex.message.buffer;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public abstract class MessageBuffer<T> {

    private final Queue<T> messageQueue = new ConcurrentLinkedQueue<>();

    public void add(T message) {
        messageQueue.add(message);
    }

    public List<T> getAll() {
        return new ArrayList<>(messageQueue);
    }

    public void clear() {
        messageQueue.clear();
    }

    public boolean isEmpty() {
        return messageQueue.isEmpty();
    }
}