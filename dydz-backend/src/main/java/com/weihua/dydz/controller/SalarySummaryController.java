package com.weihua.dydz.controller;

import com.weihua.dydz.dto.SalarySummaryResponse;
import com.weihua.dydz.service.SalarySummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/salary-summary")
@RequiredArgsConstructor
public class SalarySummaryController {
    private final SalarySummaryService salarySummaryService;

    @GetMapping
    public List<SalarySummaryResponse> summary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return salarySummaryService.summary(from, to);
    }
}
