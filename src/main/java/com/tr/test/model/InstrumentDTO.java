package com.tr.test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstrumentDTO {
    private String type;
    private InstrumentData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InstrumentData {
        private String description;
        private String isin;
    }
}
