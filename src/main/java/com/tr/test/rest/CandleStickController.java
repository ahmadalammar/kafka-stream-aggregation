package com.tr.test.rest;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tr.test.model.CandlestickDTO;
import com.tr.test.service.CandleStickService;

@RestController
@RequestMapping("/candlesticks")
public class CandleStickController {
    @Autowired
    CandleStickService candleStickService;

    @GetMapping
    public ResponseEntity<?> getCandlestick(@RequestParam("isin") String isin) {
        Optional<CandlestickDTO> candlestick = candleStickService.getCandleStickByISIN(isin);

        if (candlestick.isPresent()) {
            return new ResponseEntity<>(candlestick.get(), HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ISIN not found! or expired");
        }
    }
}
