package com.github.cortex.photo;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import com.github.cortex.message.buffer.MessageBuffer;

import java.util.List;
import java.util.Optional;

@Component
public class PendingPhotoMessageBuffer extends MessageBuffer<Message> {

    public Optional<Message> consumeLast() {
        List<Message> all = getAllAndClear();
        if (all.isEmpty()) return Optional.empty();

        Message last = all.getLast();
        return Optional.of(last);
    }
}