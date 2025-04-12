package com.github.cortex.service.message;

import com.github.cortex.messaging.controll.UserMessageController;
import com.github.cortex.service.message.utils.MessageSerializer;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
public class MessageScheduler {

    private final MessageSerializer serializer;
    private final MessageStorage storage;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final UserMessageController userMessageController;

    @Autowired
    public MessageScheduler(
            MessageSerializer serializer,
            MessageStorage storage,
     		@Value("${bot.message_recording_delay}") int messageRecordingDelay,
            UserMessageController userMessageController
    ) {
        this.serializer = serializer;
        this.storage = storage;
        this.userMessageController = userMessageController;
        scheduler.scheduleAtFixedRate(
                this::serializeAndProduce,
                0, messageRecordingDelay,
                TimeUnit.SECONDS
        );
    }

    private void serializeAndProduce() {
        if (!storage.isEmpty()) {
            String json = serializer.serialize(storage.getAndClear());
            log.info("Serialized messages: {}", json);
            userMessageController.produce(json);
        }
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }
}