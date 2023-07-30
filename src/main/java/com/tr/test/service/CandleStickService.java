package com.tr.test.service;

import java.util.Optional;

import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;

import com.tr.test.model.CandlestickDTO;

@Service
public class CandleStickService {

    @Autowired
    StreamsBuilderFactoryBean kafkaStreamsFactory;

    public Optional<CandlestickDTO> getCandleStickByISIN(String isin) {
        ReadOnlyKeyValueStore<String, CandlestickDTO> keyValueStore = kafkaStreamsFactory
            .getKafkaStreams()
            .store(StoreQueryParameters.fromNameAndType("keyvalue-candlestick-aggregator", QueryableStoreTypes.keyValueStore()));

        return Optional.ofNullable(keyValueStore.get(isin));
    }
}
