package com.weihua.dydz.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class OrderTemplateService {
    public byte[] buildXlsxTemplate() {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("orders");
            Row header = sheet.createRow(0);
            String[] headers = new String[]{
                    "order_no",
                    "owner",
                    "material",
                    "spec",
                    "order_date(yyyy-MM-dd)",
                    "item_product",
                    "item_piece_count",
                    "item_piece_weight"
            };
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
                sheet.autoSizeColumn(i);
            }
            Row sample = sheet.createRow(1);
            sample.createCell(0).setCellValue("DZ-20260212-001");
            sample.createCell(1).setCellValue("青子");
            sample.createCell(2).setCellValue("45#钢");
            sample.createCell(3).setCellValue("Φ50×120");
            sample.createCell(4).setCellValue("2026-02-12");
            sample.createCell(5).setCellValue("法兰");
            sample.createCell(6).setCellValue(100);
            sample.createCell(7).setCellValue(0.05);
            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to build template");
        }
    }
}
