package com.tr.test.processor;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.state.KeyValueStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonSerde;

import com.tr.test.model.CandlestickDTO;
import com.tr.test.model.QuoteDTO;

public class QuoteProcessorUnitTest {

    private TopologyTestDriver testDriver;
    private StreamsBuilder streamsBuilder;
    private QuoteProcessor quoteProcessor;
    TestInputTopic<String, QuoteDTO> inputTopic;

    @BeforeEach
    public void setup() {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "candlestick-application");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        streamsBuilder = new StreamsBuilder();
        quoteProcessor = new QuoteProcessor();

        quoteProcessor.processQuoteEvents(streamsBuilder);

        Topology topology = streamsBuilder.build();
        testDriver = new TopologyTestDriver(topology, config);

        inputTopic = testDriver.createInputTopic("quote-topic", Serdes.String()
            .serializer(), new JsonSerde<>(QuoteDTO.class).serializer());
    }

    @AfterEach
    public void tearDown() {
        testDriver.close();
    }

    @Test
    public void shouldAggregateQuote_whenQuotesReceivedWithinOneMinute() {

        String isin = UUID.randomUUID()
            .toString();

        // Define the fixed date and time
        int year = 2023;
        int month = 7;
        int day = 16;
        int hour = 12;
        int minute = 30;
        int second = 0;

        LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute, second);
        ZoneId zoneId = ZoneId.of("UTC");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);

        Instant eventTime1 = zonedDateTime.toInstant();
        Instant eventTime2 = eventTime1.plus(Duration.ofSeconds(5));
        Instant eventTime3 = eventTime2.plus(Duration.ofSeconds(5));
        Instant eventTime4 = eventTime3.plus(Duration.ofSeconds(5));
        List<QuoteDTO> quoteDTOList = getDummyQuotes(isin);

        // assume that the events sent in this order
        inputTopic.pipeInput(isin, quoteDTOList.get(0), eventTime1);
        inputTopic.pipeInput(isin, quoteDTOList.get(1), eventTime2);
        inputTopic.pipeInput(isin, quoteDTOList.get(2), eventTime3);
        inputTopic.pipeInput(isin, quoteDTOList.get(3), eventTime4);


        KeyValueStore<String, CandlestickDTO> keyValueStore = testDriver.getKeyValueStore("keyvalue-candlestick-aggregator");
        CandlestickDTO candlestickDTO = keyValueStore.get(isin);

        Assertions.assertEquals(candlestickDTO.getOpenPrice(), 123.2);
        Assertions.assertEquals(candlestickDTO.getLowPrice(), 100);
        Assertions.assertEquals(candlestickDTO.getHighPrice(), 160.1);
        Assertions.assertEquals(candlestickDTO.getClosingPrice(), 160.1);
    }

    private List<QuoteDTO> getDummyQuotes(String key) {

        QuoteDTO quoteDTO1 = QuoteDTO.builder()
            .data(QuoteDTO.QuoteData.builder()
                .price(123.2)
                .isin(key)
                .build())
            .build();

        QuoteDTO quoteDTO2 = QuoteDTO.builder()
            .data(QuoteDTO.QuoteData.builder()
                .price(100)
                .isin(key)
                .build())
            .build();

        QuoteDTO quoteDTO3 = QuoteDTO.builder()
            .data(QuoteDTO.QuoteData.builder()
                .price(102.2)
                .isin(key)
                .build())
            .build();

        QuoteDTO quoteDTO4 = QuoteDTO.builder()
            .data(QuoteDTO.QuoteData.builder()
                .price(160.1)
                .isin(key)
                .build())
            .build();

        QuoteDTO quoteDTO5 = QuoteDTO.builder()
            .data(QuoteDTO.QuoteData.builder()
                .price(260.1)
                .isin(key)
                .build())
            .build();

        return List.of(quoteDTO1, quoteDTO2, quoteDTO3, quoteDTO4, quoteDTO5);
    }
}
