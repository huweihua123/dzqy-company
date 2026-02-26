package com.weihua.dydz.controller;

import com.weihua.dydz.service.OrderImportService;
import com.weihua.dydz.service.OrderTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderImportController {
    private final OrderImportService orderImportService;
    private final OrderTemplateService orderTemplateService;

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String importExcel(@RequestParam("file") MultipartFile file) {
        int count = orderImportService.importExcel(file);
        return "Imported orders: " + count;
    }

    @GetMapping("/template")
    public byte[] template() {
        String header = String.join(",",
                "order_no",
                "owner",
                "material",
                "spec",
                "order_date(yyyy-MM-dd)",
                "item_product",
                "item_piece_count",
                "item_piece_weight"
        );
        return (header + "\n").getBytes(StandardCharsets.UTF_8);
    }

    @GetMapping(value = "/template.csv", produces = "text/csv")
    public org.springframework.http.ResponseEntity<byte[]> templateCsv() {
        byte[] bytes = template();
        return org.springframework.http.ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=order_template.csv")
                .body(bytes);
    }

    @GetMapping(value = "/template.xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public org.springframework.http.ResponseEntity<byte[]> templateXlsx() {
        byte[] bytes = orderTemplateService.buildXlsxTemplate();
        return org.springframework.http.ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=order_template.xlsx")
                .body(bytes);
    }
}
