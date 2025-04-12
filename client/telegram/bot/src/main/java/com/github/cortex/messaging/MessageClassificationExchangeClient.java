package com.github.cortex.messaging;

import com.github.cortex.messaging.dto.AgroMessage;
import com.github.cortex.messaging.dto.MessageClassification;
import com.github.cortex.messaging.dto.MessageClassificationResponse;
import com.github.cortex.exception.messaging.MessageClassificationExchangeException;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Collections;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class MessageClassificationExchangeClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${proxy.post.url}")
    private String POST_URL;

    public List<MessageClassification> classifyMessages(List<AgroMessage> agroMessages) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<AgroMessage>> requestEntity = new HttpEntity<>(agroMessages, headers);
        try {
            ResponseEntity<MessageClassificationResponse> response = restTemplate.exchange(
                    POST_URL,
                    HttpMethod.POST,
                    requestEntity,
                    MessageClassificationResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() || response.hasBody()) {
                List<MessageClassification> classifications = response.getBody().getResponse();
                log.info("Classified messages received: {}", classifications);
                return classifications;
            }
        } catch (RestClientException ex) { throw createExchangeException(agroMessages, ex); }

        return Collections.emptyList();
    }

    private MessageClassificationExchangeException createExchangeException(List<AgroMessage> agroMessages, RestClientException ex) {
        log.error("Failed to classify messages: {}", agroMessages, ex);
        return new MessageClassificationExchangeException("Request: " + agroMessages);
    }
}