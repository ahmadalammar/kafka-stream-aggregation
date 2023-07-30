package com.tr.test.model;

import lombok.Data;

import java.time.Instant;

@Data
public class CandlestickDTO {
    private Instant openTimestamp;
    private Instant closeTimestamp;
    private double openPrice;
    private double highPrice;
    private double lowPrice;
    private double closingPrice;
}
