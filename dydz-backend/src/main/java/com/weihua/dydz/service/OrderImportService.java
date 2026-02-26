package com.weihua.dydz.service;

import com.weihua.dydz.domain.*;
import com.weihua.dydz.dto.OrderCreateRequest;
import com.weihua.dydz.dto.OrderItemRequest;
import com.weihua.dydz.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderImportService {
    private final OwnerRepository ownerRepository;
    private final MaterialRepository materialRepository;
    private final SpecRepository specRepository;
    private final OrderService orderService;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public int importExcel(MultipartFile file) {
        Map<String, List<RowData>> grouped = new LinkedHashMap<>();
        List<ImportError> errors = new ArrayList<>();
        try (InputStream in = file.getInputStream(); Workbook wb = WorkbookFactory.create(in)) {
            for (int s = 0; s < wb.getNumberOfSheets(); s++) {
                Sheet sheet = wb.getSheetAt(s);
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;
                    String orderNo = getString(row, 0);
                    if (orderNo == null || orderNo.isBlank()) continue;
                    RowData data = parseRow(row, errors, i + 1);
                    grouped.computeIfAbsent(orderNo, k -> new ArrayList<>()).add(data);
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Excel file: " + e.getMessage());
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Import has errors, use preview to check.");
        }

        int created = 0;
        for (Map.Entry<String, List<RowData>> entry : grouped.entrySet()) {
            List<RowData> rows = entry.getValue();
            RowData first = rows.get(0);
            Owner owner = ownerRepository.findByName(first.ownerName);
            if (owner == null) {
                owner = new Owner();
                owner.setName(first.ownerName);
                owner = ownerRepository.save(owner);
            }
            if (owner.getUnitPrice() == null || owner.getUnitPrice().signum() <= 0) {
                throw new IllegalArgumentException("Owner unit price not set: " + owner.getName());
            }
            List<OrderItemRequest> items = new ArrayList<>();
            for (RowData r : rows) {
                Material material = null;
                if (r.materialName != null && !r.materialName.isBlank()) {
                    material = materialRepository.findByName(r.materialName);
                    if (material == null) {
                        material = new Material();
                        material.setName(r.materialName);
                        material.setMultiplier(java.math.BigDecimal.ONE);
                        material = materialRepository.save(material);
                    }
                }
                Spec spec = null;
                if (r.specName != null && !r.specName.isBlank()) {
                    spec = specRepository.findByName(r.specName);
                    if (spec == null) {
                        spec = new Spec();
                        spec.setName(r.specName);
                        spec = specRepository.save(spec);
                    }
                }
                items.add(new OrderItemRequest(
                        r.productName,
                        material != null ? material.getId() : null,
                        spec != null ? spec.getId() : null,
                        r.pieceCount,
                        r.pieceWeight
                ));
            }

            OrderCreateRequest req = new OrderCreateRequest(
                    first.orderNo,
                    owner.getId(),
                    first.orderDate,
                    items
            );
            orderService.create(req);
            created++;
        }
        return created;
    }

    public List<ImportError> previewErrors(MultipartFile file) {
        List<ImportError> errors = new ArrayList<>();
        try (InputStream in = file.getInputStream(); Workbook wb = WorkbookFactory.create(in)) {
            for (int s = 0; s < wb.getNumberOfSheets(); s++) {
                Sheet sheet = wb.getSheetAt(s);
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;
                    String orderNo = getString(row, 0);
                    if (orderNo == null || orderNo.isBlank()) continue;
                    parseRow(row, errors, i + 1);
                }
            }
        } catch (Exception e) {
            errors.add(new ImportError(0, "文件解析失败: " + e.getMessage()));
        }
        return errors;
    }

    private RowData parseRow(Row row, List<ImportError> errors, int rowNum) {
        RowData data = new RowData();
        data.orderNo = getString(row, 0);
        data.ownerName = getString(row, 1);
        data.materialName = getString(row, 2);
        data.specName = getString(row, 3);
        data.orderDate = getDate(row, 4);
        data.productName = getString(row, 5);
        data.pieceCount = getDecimal(row, 6);
        data.pieceWeight = getDecimal(row, 7);
        if (data.ownerName == null || data.ownerName.isBlank()) {
            errors.add(new ImportError(rowNum, "货主为空"));
        }
        if (data.productName == null || data.productName.isBlank()) {
            errors.add(new ImportError(rowNum, "产品名称为空"));
        }
        if (data.pieceCount == null || data.pieceCount.signum() <= 0) {
            errors.add(new ImportError(rowNum, "件数必须大于0"));
        }
        if (data.pieceWeight == null || data.pieceWeight.signum() <= 0) {
            errors.add(new ImportError(rowNum, "单件重量必须大于0"));
        }
        return data;
    }

    private String getString(Row row, int idx) {
        Cell cell = row.getCell(idx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    private BigDecimal getDecimal(Row row, int idx) {
        Cell cell = row.getCell(idx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return BigDecimal.ZERO;
        if (cell.getCellType() == CellType.STRING) {
            String v = cell.getStringCellValue();
            if (v == null || v.isBlank()) return BigDecimal.ZERO;
            return new BigDecimal(v.trim());
        }
        return BigDecimal.valueOf(cell.getNumericCellValue());
    }

    private LocalDate getDate(Row row, int idx) {
        Cell cell = row.getCell(idx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return LocalDate.now();
        if (DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }
        String v = getString(row, idx);
        if (v == null || v.isBlank()) return LocalDate.now();
        return LocalDate.parse(v, DATE_FMT);
    }

    private static class RowData {
        String orderNo;
        String ownerName;
        String materialName;
        String specName;
        LocalDate orderDate;
        String productName;
        BigDecimal pieceCount;
        BigDecimal pieceWeight;
    }

    public record ImportError(int row, String message) {}
}
