package com.weihua.dydz.service;

import com.weihua.dydz.dto.ReportPoint;
import com.weihua.dydz.dto.SalarySummaryResponse;
import com.weihua.dydz.dto.OwnerReportPoint;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReportExportService {
    public byte[] exportProduction(List<ReportPoint> points) {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("production");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("date");
            header.createCell(1).setCellValue("net_tonnage");
            int i = 1;
            for (ReportPoint p : points) {
                Row row = sheet.createRow(i++);
                row.createCell(0).setCellValue(p.date().toString());
                row.createCell(1).setCellValue(p.value().doubleValue());
            }
            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalArgumentException("Export failed");
        }
    }

    public byte[] exportOrders(List<ReportPoint> points) {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("orders");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("date");
            header.createCell(1).setCellValue("total_fee");
            int i = 1;
            for (ReportPoint p : points) {
                Row row = sheet.createRow(i++);
                row.createCell(0).setCellValue(p.date().toString());
                row.createCell(1).setCellValue(p.value().doubleValue());
            }
            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalArgumentException("Export failed");
        }
    }

    public byte[] exportSalary(List<SalarySummaryResponse> list) {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("salary");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("worker");
            header.createCell(1).setCellValue("role");
            header.createCell(2).setCellValue("pay_type");
            header.createCell(3).setCellValue("earned");
            header.createCell(4).setCellValue("paid");
            header.createCell(5).setCellValue("owed");
            int i = 1;
            for (SalarySummaryResponse r : list) {
                Row row = sheet.createRow(i++);
                row.createCell(0).setCellValue(r.workerName());
                row.createCell(1).setCellValue(r.role());
                row.createCell(2).setCellValue(r.payType().name());
                row.createCell(3).setCellValue(r.earnedAmount().doubleValue());
                row.createCell(4).setCellValue(r.paidAmount().doubleValue());
                row.createCell(5).setCellValue(r.owedAmount().doubleValue());
            }
            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalArgumentException("Export failed");
        }
    }

    public byte[] exportOwners(List<OwnerReportPoint> list) {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("owners");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("owner");
            header.createCell(1).setCellValue("total_fee");
            int i = 1;
            for (OwnerReportPoint r : list) {
                Row row = sheet.createRow(i++);
                row.createCell(0).setCellValue(r.ownerName());
                row.createCell(1).setCellValue(r.totalFee().doubleValue());
            }
            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalArgumentException("Export failed");
        }
    }
}
