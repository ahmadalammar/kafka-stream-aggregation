package com.tr.test.listener;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tr.test.model.QuoteDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class InstrumentsWSListener extends TextWebSocketHandler {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    InstrumentsWSListener(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Connected to WebSocket server");
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WS connection error", exception);
        super.handleTransportError(session, exception);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        QuoteDTO quoteDTO = objectMapper.readValue(message.getPayload(), QuoteDTO.class);
        kafkaTemplate.send("instrument-topic", quoteDTO.getData()
            .getIsin(), quoteDTO);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Disconnected from WebSocket server");
    }
}
