package com.github.cortex.classification.service;

import com.github.cortex.agro.dto.Agronomist;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.github.cortex.agro.dto.AgroMessage;
import com.github.cortex.classification.dto.MessageClassification;
import com.github.cortex.classification.dto.MessageClassificationResponse;
import com.github.cortex.exception.classifiaction.MessageClassificationExchangeException;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class MessageClassificationExchangeClientTest {

    @Test
    void should_SuccessfulResponse_ReturnsClassifications() {

        String postURL = "http://127.0.0.1:8000/messages/proc_many";
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        MessageClassificationExchangeClient client = new MessageClassificationExchangeClient(restTemplate, postURL);

        Agronomist agronomist = new Agronomist("some_name", "id");
        List<AgroMessage> messages = List.of(new AgroMessage(
                agronomist,
                Optional.of("SOME_TEXT"),
                Optional.empty()
        ));
        List<MessageClassification> mockResponse = List.of(
                new MessageClassification(
                        LocalDateTime.now(),
                        "Dep",
                        "Oper",
                        "Plant",
                        10,
                        10,
                        10,
                        10
                )
        );
        MessageClassificationResponse responseBody = new MessageClassificationResponse();
        responseBody.setResponse(mockResponse);

        ResponseEntity<MessageClassificationResponse> responseEntity =
                new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(postURL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(MessageClassificationResponse.class)
        )).thenReturn(responseEntity);

        List<MessageClassification> result = client.executeClassification(messages);
        assertEquals(mockResponse, result);
    }

    @Test
    void executeClassification_SuccessfulNoBody_WhenEmptyList() {

        String postURL = "http://127.0.0.1:8000/messages/proc_many";
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        ResponseEntity<MessageClassificationResponse> responseEntity =
                new ResponseEntity<>(null, HttpStatus.OK);

        MessageClassificationExchangeClient client = new MessageClassificationExchangeClient(restTemplate, postURL);

        when(restTemplate.exchange(
                eq(postURL),
                eq(HttpMethod.POST),
                any(),
                eq(MessageClassificationResponse.class)
        )).thenReturn(responseEntity);

        List<MessageClassification> result = client.executeClassification(List.of());
        assertTrue(result.isEmpty());
    }

    @Test
    void executeClassification_Non2xxResponse_WhenEmptyList() {

        String postURL = "http://127.0.0.1:8000/messages/proc_many";
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        ResponseEntity<MessageClassificationResponse> responseEntity =
                new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        MessageClassificationExchangeClient client = new MessageClassificationExchangeClient(restTemplate, postURL);
        when(restTemplate.exchange(
                eq(postURL),
                eq(HttpMethod.POST),
                any(),
                eq(MessageClassificationResponse.class)
        )).thenReturn(responseEntity);

        List<MessageClassification> result = client.executeClassification(List.of());
        assertTrue(result.isEmpty());
    }

    @Test
    void executeClassification_RestClientException_throwsCustomException() {
        String postURL = "http://127.0.0.1:8000/messages/proc_many";
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);

        MessageClassificationExchangeClient client = new MessageClassificationExchangeClient(restTemplate, postURL);
        when(restTemplate.exchange(
                eq(postURL),
                eq(HttpMethod.POST),
                any(),
                eq(MessageClassificationResponse.class)
        )).thenThrow(new RestClientException("Timeout"));

        Agronomist agronomist = new Agronomist("some_name", "id");

        List<AgroMessage> messages = List.of(new AgroMessage(
                agronomist,
                Optional.of("SOME_TEXT"),
                Optional.empty()
        ));
        assertThrows(MessageClassificationExchangeException.class, () -> {
            client.executeClassification(messages);
        });
    }
}