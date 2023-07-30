package com.tr.test.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.tr.test.listener.InstrumentsWSListener;
import com.tr.test.listener.QuoteWSListener;

@Configuration
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    QuoteWSListener quoteListener;
    @Autowired
    InstrumentsWSListener instrumentsWSListener;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(quoteListener, "/quotes");
        registry.addHandler(instrumentsWSListener, "/instruments");
    }

    @Bean
    public StandardWebSocketClient getWebSocketClient(){
        return new StandardWebSocketClient();
    }
}
