package com.github.cortex.classification.service;

import com.github.cortex.agro.dto.AgroMessage;
import com.github.cortex.agro.utils.AgroMessageUtils;
import com.github.cortex.classification.dto.MessageClassification;
import com.github.cortex.classification.dto.MessageClassificationResponse;
import com.github.cortex.exception.classifiaction.MessageClassificationExchangeException;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Collections;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class MessageClassificationExchangeClient {

    private final String postUrl;
    private final RestTemplate restTemplate;


    @Autowired
    public MessageClassificationExchangeClient(
            RestTemplate restTemplate,
            @Value("${proxy.post.url}") String postUrl
    ) {
        this.restTemplate = restTemplate;
        this.postUrl = postUrl;
    }

    public List<MessageClassification> executeClassification(List<AgroMessage> agroMessages) throws MessageClassificationExchangeException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<AgroMessage>> requestEntity = new HttpEntity<>(agroMessages, headers);

        try {
            ResponseEntity<MessageClassificationResponse> response = restTemplate.exchange(
                    postUrl,
                    HttpMethod.POST,
                    requestEntity,
                    MessageClassificationResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
                List<MessageClassification> classifications = response.getBody().getResponse();
                log.info("Classified messages received: {}", classifications);
                return classifications;
            }
        } catch (RestClientException ex) { throw createExchangeException(agroMessages, ex); }

        return Collections.emptyList();
    }

    private MessageClassificationExchangeException createExchangeException(List<AgroMessage> agroMessages, RestClientException ex) {
        String data = AgroMessageUtils.extractFieldData(agroMessages);
        log.error("Failed to classify messages:\n{}", data, ex);
        return new MessageClassificationExchangeException("Request: " + data);
    }
}