package com.github.cortex.agro.service;

import com.github.cortex.agro.dto.AgroMessage;
import com.github.cortex.exception.classifiaction.EmptyClassifiedMessagesException;
import com.github.cortex.exception.classifiaction.MessageClassificationExchangeException;
import jakarta.annotation.PreDestroy;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.github.cortex.message.buffer.MessageBuffer;
import com.github.cortex.classification.service.MessageClassificationPersistService;

@Log4j2
@Component
public class AgroMessageProcessingScheduler {

    private final MessageClassificationPersistService messageClassificationPersistService;
    private final MessageBuffer<AgroMessage> agroMessageBuffer;
    private final UnclassifiedMessageService unclassifiedMessageService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    public AgroMessageProcessingScheduler(
            MessageClassificationPersistService messageClassificationPersistService,
            MessageBuffer<AgroMessage> agroMessageBuffer,
            UnclassifiedMessageService unclassifiedMessageService,
     		@Value("${bot.request_delay}") int requestDelay
    ) {
        this.messageClassificationPersistService = messageClassificationPersistService;
        this.agroMessageBuffer = agroMessageBuffer;
        this.unclassifiedMessageService = unclassifiedMessageService;

        scheduler.scheduleAtFixedRate(
                this::processPendingMessages,
                0, requestDelay,
                TimeUnit.SECONDS
        );
    }

    private void processPendingMessages() {

        List<AgroMessage> messages = agroMessageBuffer.getAll();
        if(messages.isEmpty()) return;

        try {
            messageClassificationPersistService.execute(messages);
        } catch (EmptyClassifiedMessagesException | MessageClassificationExchangeException ex) {
        	unclassifiedMessageService.record(messages);
        } catch (Exception ex) {
            log.error("Unexpected error during agro message classification!", ex);
        } finally {
            agroMessageBuffer.clear();
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