package com.github.cortex.service.message;

import lombok.extern.log4j.Log4j2;
import jakarta.annotation.PreDestroy;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.github.cortex.messaging.dto.AgroMessage;
import com.github.cortex.messaging.dto.MessageClassification;
import com.github.cortex.messaging.MessageClassificationExchangeClient;

@Log4j2
@Component
public class AgroMessageScheduler {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final AgroMessageRepository agroMessageRepository;
    private final MessageClassificationExchangeClient exchangeClient;

    @Autowired
    public AgroMessageScheduler(
            AgroMessageRepository agroMessageRepository,
            MessageClassificationExchangeClient exchangeClient,
     		@Value("${bot.request_delay}") int requestDelay
    ) {
        this.agroMessageRepository = agroMessageRepository;
        this.exchangeClient = exchangeClient;

        scheduler.scheduleAtFixedRate(
                this::processMessages,
                0, requestDelay,
                TimeUnit.SECONDS
        );
    }

    private void processMessages() {
        if (!agroMessageRepository.isEmpty()) {
            List<AgroMessage> messages = agroMessageRepository.getAndClear();
            List<MessageClassification> classifiedMessages = exchangeClient.classifyMessages(messages);
            if (!classifiedMessages.isEmpty()) {
                //TODO: реализация дальнейшей обработки классифицированных данных
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }
}