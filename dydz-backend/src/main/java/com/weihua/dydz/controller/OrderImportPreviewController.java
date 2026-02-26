package com.weihua.dydz.controller;

import com.weihua.dydz.service.OrderImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderImportPreviewController {
    private final OrderImportService orderImportService;

    @PostMapping(value = "/import/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<OrderImportService.ImportError> preview(@RequestParam("file") MultipartFile file) {
        return orderImportService.previewErrors(file);
    }
}
