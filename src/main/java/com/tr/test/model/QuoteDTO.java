package com.tr.test.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuoteDTO {
    private QuoteData data;

    private String type;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class QuoteData {
        private String isin;
        private double price;
    }
}
