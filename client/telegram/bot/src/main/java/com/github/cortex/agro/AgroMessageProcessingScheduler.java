package com.github.cortex.agro;

import jakarta.annotation.PreDestroy;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.github.cortex.message.MessageBuffer;
import com.github.cortex.classification.service.MessageClassificationPersistService;


// TODO: тест

@Component
public class AgroMessageProcessingScheduler {

    private final MessageClassificationPersistService messageClassificationPersistService;
    private final MessageBuffer<AgroMessage> agroMessageBuffer;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    public AgroMessageProcessingScheduler(
            MessageClassificationPersistService messageClassificationPersistService,
            MessageBuffer<AgroMessage> agroMessageBuffer,
     		@Value("${bot.request_delay}") int requestDelay
    ) {
        this.messageClassificationPersistService = messageClassificationPersistService;
        this.agroMessageBuffer = agroMessageBuffer;

        scheduler.scheduleAtFixedRate(
                this::processPendingMessages,
                0, requestDelay,
                TimeUnit.SECONDS
        );
    }

    private void processPendingMessages() {
        if (!agroMessageBuffer.isEmpty()) {
            List<AgroMessage> messages = agroMessageBuffer.getAndClear();
            messageClassificationPersistService.execute(messages);
        }
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdown();
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}