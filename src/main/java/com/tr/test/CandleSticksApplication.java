package com.tr.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import com.tr.test.listener.QuoteWSListener;

@SpringBootApplication
@EnableWebSocket
public class CandleSticksApplication implements CommandLineRunner {
    @Value("${partner.service.url}")
    private String partnerServerUrl;
    @Autowired
    private QuoteWSListener quoteListener;
    @Autowired
    StandardWebSocketClient webSocketClient;

    public static void main(String[] args) {
        SpringApplication.run(CandleSticksApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        webSocketClient.doHandshake(quoteListener, partnerServerUrl).get();
    }
}
