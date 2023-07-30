package com.tr.test.processor;

import java.time.Duration;
import java.time.Instant;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import com.tr.test.model.CandlestickDTO;
import com.tr.test.model.QuoteDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class QuoteProcessor {

    @Autowired
    public KStream<String, CandlestickDTO> processQuoteEvents(StreamsBuilder streamsBuilder) {

        KStream<String, QuoteDTO> quoteStream = streamsBuilder.stream("quote-topic", Consumed.with(Serdes.String(), new JsonSerde<>(QuoteDTO.class)));

        TimeWindows tumblingWindow = TimeWindows.of(Duration.ofMinutes(1));

        Materialized<String, CandlestickDTO, WindowStore<Bytes, byte[]>> windowStoreMaterialized = Materialized.<String, CandlestickDTO, WindowStore<Bytes, byte[]>>as("window-candlestick-aggregator")
            .withKeySerde(Serdes.String())
            .withValueSerde(new JsonSerde<>(CandlestickDTO.class))
            .withRetention(Duration.ofMinutes(30));

        Materialized<String, CandlestickDTO, KeyValueStore<Bytes, byte[]>> keyValueStoreMaterialized = Materialized.<String, CandlestickDTO, KeyValueStore<Bytes, byte[]>>as("keyvalue-candlestick-aggregator")
            .withKeySerde(Serdes.String())
            .withValueSerde(new JsonSerde<>(CandlestickDTO.class))
            .withRetention(Duration.ofMinutes(30));

        KStream<String, CandlestickDTO> candlestickKStream = quoteStream.groupByKey()
            .windowedBy(tumblingWindow)
            .aggregate(CandlestickDTO::new, (key, value, aggregate) -> candleStickAggregator(value, aggregate), windowStoreMaterialized)
            .mapValues((windowedKey, candlestick) -> {

                // set open time and close time from window aggregator as its more accurate
                candlestick.setCloseTimestamp(windowedKey.window().endTime());
                candlestick.setOpenTimestamp(windowedKey.window().startTime());

                log.info("ISIN = " + windowedKey.key() + " CandleStick = " + candlestick);

                return candlestick;
            })
            .toStream()
            .map((windowedKey, value) -> new KeyValue<>(windowedKey.key(), value));

        //candlestickKStream.toTable(keyValueStoreMaterialized);

        candlestickKStream.to("candlestick-event");


        // kafka - connect
        return candlestickKStream;
    }

    public CandlestickDTO candleStickAggregator(QuoteDTO value, CandlestickDTO aggregate) {

        if (aggregate.getOpenTimestamp() == null) {
            aggregate.setOpenTimestamp(Instant.now());
            aggregate.setOpenPrice(value.getData().getPrice());
        }

        if (value.getData().getPrice() > aggregate.getHighPrice()) {
            aggregate.setHighPrice(value.getData().getPrice());
        }

        if (aggregate.getLowPrice() == 0.0 || value.getData().getPrice() < aggregate.getLowPrice()) {
            aggregate.setLowPrice(value.getData().getPrice());
        }

        aggregate.setClosingPrice(value.getData().getPrice());

        return aggregate;
    }
}
