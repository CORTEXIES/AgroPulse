package com.github.cortex.photo;

import com.github.cortex.message.buffer.MessageBuffer;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.List;
import java.util.Optional;

@Component
public class PendingPhotoMessageBuffer extends MessageBuffer<Message> {

    public Optional<Message> consumeLast() {
        List<Message> all = getAll();
        if (all.isEmpty()) return Optional.empty();

        Message last = all.getLast();
        clear();
        return Optional.of(last);
    }
}