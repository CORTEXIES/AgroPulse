package com.github.cortex.service.message;

import com.github.cortex.messaging.MessageClassificationExchangeClient;
import com.github.cortex.messaging.dto.MessageClassification;
import lombok.extern.log4j.Log4j2;
import jakarta.annotation.PreDestroy;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.cortex.messaging.dto.AgroMessage;

@Log4j2
@Component
public class MessageScheduler {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final MessageRepository messageRepository;
    private final MessageClassificationExchangeClient exchangeClient;

    @Autowired
    public MessageScheduler(
            MessageRepository messageRepository,
            MessageClassificationExchangeClient exchangeClient,
     		@Value("${bot.request_delay}") int requestDelay
    ) {
        this.messageRepository = messageRepository;
        this.exchangeClient = exchangeClient;

        scheduler.scheduleAtFixedRate(
                this::handleAgroMessages,
                0, requestDelay,
                TimeUnit.SECONDS
        );
    }

    private void handleAgroMessages() {
        if (!messageRepository.isEmpty()) {
            List<AgroMessage> request = messageRepository.getAndClear();
            List<MessageClassification> classifiedMessages = exchangeClient.classifyMessages(request);
        }
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }
}