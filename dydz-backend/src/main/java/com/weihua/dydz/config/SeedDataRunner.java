package com.weihua.dydz.config;

import com.weihua.dydz.domain.*;
import com.weihua.dydz.dto.OrderCreateRequest;
import com.weihua.dydz.dto.OrderItemRequest;
import com.weihua.dydz.repository.*;
import com.weihua.dydz.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeedDataRunner implements CommandLineRunner {
    private final OwnerRepository ownerRepository;
    private final MaterialRepository materialRepository;
    private final SpecRepository specRepository;
    private final WorkerRoleRepository workerRoleRepository;
    private final WorkerRepository workerRepository;
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OwnerReceiptRepository ownerReceiptRepository;
    private final ProductionRecordRepository productionRecordRepository;
    private final AttendanceRepository attendanceRepository;

    @Override
    public void run(String... args) {
        if (!isEmptyDatabase()) {
            return;
        }
        seedOwners();
        seedMaterials();
        seedSpecs();
        seedWorkerRoles();
        seedWorkers();
        seedOrdersAndReceipts();
        seedAttendanceAndProduction();
    }

    private boolean isEmptyDatabase() {
        return ownerRepository.count() == 0
                && materialRepository.count() == 0
                && specRepository.count() == 0
                && workerRoleRepository.count() == 0
                && workerRepository.count() == 0;
    }

    private void seedOwners() {
        if (!ownerRepository.existsByName("青子")) {
            Owner owner = new Owner();
            owner.setName("青子");
            owner.setUnitPrice(new BigDecimal("800"));
            ownerRepository.save(owner);
        }
        if (!ownerRepository.existsByName("四狗")) {
            Owner owner = new Owner();
            owner.setName("四狗");
            owner.setUnitPrice(new BigDecimal("850"));
            ownerRepository.save(owner);
        }
    }

    private void seedMaterials() {
        if (!materialRepository.existsByName("45#钢")) {
            Material material = new Material();
            material.setName("45#钢");
            material.setCode("45#");
            material.setMultiplier(new BigDecimal("1"));
            materialRepository.save(material);
        }
        if (!materialRepository.existsByName("Q235")) {
            Material material = new Material();
            material.setName("Q235");
            material.setCode("Q235");
            material.setMultiplier(new BigDecimal("1"));
            materialRepository.save(material);
        }
        if (!materialRepository.existsByName("20Cr")) {
            Material material = new Material();
            material.setName("20Cr");
            material.setCode("20Cr");
            material.setMultiplier(new BigDecimal("1"));
            materialRepository.save(material);
        }
    }

    private void seedSpecs() {
        if (!specRepository.existsByName("Φ50×120")) {
            Spec spec = new Spec();
            spec.setName("Φ50×120");
            spec.setDescription("直径50 长度120");
            specRepository.save(spec);
        }
        if (!specRepository.existsByName("Φ80×160")) {
            Spec spec = new Spec();
            spec.setName("Φ80×160");
            spec.setDescription("直径80 长度160");
            specRepository.save(spec);
        }
        if (!specRepository.existsByName("Φ120×200")) {
            Spec spec = new Spec();
            spec.setName("Φ120×200");
            spec.setDescription("直径120 长度200");
            specRepository.save(spec);
        }
    }

    private void seedWorkerRoles() {
        ensureRole("MASTER", "大师傅");
        ensureRole("SECOND_MASTER", "二师傅");
        ensureRole("OPERATOR", "操作机工人");
        ensureRole("FORGING_HELPER", "锻造小工");
        ensureRole("HAMMER", "司锤工");
        ensureRole("SAW", "锯料工");
        ensureRole("LATHE", "车工");
    }

    private void seedWorkers() {
        createPieceWorker("二黄毛", "MASTER", new BigDecimal("120"));
        createPieceWorker("二冰", "SECOND_MASTER", new BigDecimal("110"));
        createPieceWorker("和平", "OPERATOR", new BigDecimal("110"));
        createPieceWorker("旦旦", "FORGING_HELPER", new BigDecimal("80"));
        createPieceWorker("花子", "FORGING_HELPER", new BigDecimal("80"));
        createPieceWorker("锤锤", "HAMMER", new BigDecimal("90"));
    }

    private void ensureRole(String code, String name) {
        if (workerRoleRepository.findByCode(code).isEmpty()) {
            WorkerRole role = new WorkerRole();
            role.setCode(code);
            role.setName(name);
            workerRoleRepository.save(role);
        }
    }

    private void createPieceWorker(String name, String roleCode, BigDecimal tonPrice) {
        if (workerRepository.existsByName(name)) {
            return;
        }
        WorkerRole role = workerRoleRepository.findByCode(roleCode)
                .orElseThrow(() -> new IllegalStateException("Worker role missing: " + roleCode));
        Worker worker = new Worker();
        worker.setName(name);
        worker.setRole(role);
        worker.setPayType(PayType.PIECE);
        worker.setTonPrice(tonPrice);
        worker.setStatus(WorkerStatus.ACTIVE);
        workerRepository.save(worker);
    }

    private void seedOrdersAndReceipts() {
        if (orderRepository.existsByOrderNo("DZ-20260210-001")) {
            return;
        }
        Owner ownerA = ownerRepository.findAll().stream().filter(o -> "青子".equals(o.getName())).findFirst().orElseThrow();
        Owner ownerB = ownerRepository.findAll().stream().filter(o -> "四狗".equals(o.getName())).findFirst().orElseThrow();
        Material material = materialRepository.findAll().stream().findFirst().orElse(null);
        Spec spec = specRepository.findAll().stream().findFirst().orElse(null);

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // 订单1：青子 计划毛重 10.0 吨 = 轴类(120*0.05=6.0) + 法兰(80*0.05=4.0)
        OrderCreateRequest order1 = new OrderCreateRequest(
                "DZ-20260210-001",
                ownerA.getId(),
                today,
                List.of(
                        new OrderItemRequest("轴类件", material != null ? material.getId() : null, spec != null ? spec.getId() : null, new BigDecimal("120"), new BigDecimal("0.05")),
                        new OrderItemRequest("法兰件", material != null ? material.getId() : null, spec != null ? spec.getId() : null, new BigDecimal("80"), new BigDecimal("0.05"))
                )
        );
        orderService.create(order1);

        // 订单2：青子 计划毛重 6.0 吨 = 套筒(100*0.06)
        OrderCreateRequest order2 = new OrderCreateRequest(
                "DZ-20260210-002",
                ownerA.getId(),
                today,
                List.of(
                        new OrderItemRequest("套筒件", material != null ? material.getId() : null, spec != null ? spec.getId() : null, new BigDecimal("100"), new BigDecimal("0.06"))
                )
        );
        orderService.create(order2);

        // 订单3：四狗 计划毛重 7.2 吨 = 齿轮坯(80*0.09)
        OrderCreateRequest order3 = new OrderCreateRequest(
                "DZ-20260210-003",
                ownerB.getId(),
                yesterday,
                List.of(
                        new OrderItemRequest("齿轮坯", material != null ? material.getId() : null, spec != null ? spec.getId() : null, new BigDecimal("80"), new BigDecimal("0.09"))
                )
        );
        orderService.create(order3);

        OwnerReceipt r1 = new OwnerReceipt();
        r1.setOwner(ownerA);
        r1.setPayDate(today);
        r1.setAmount(new BigDecimal("5000"));
        r1.setRemark("部分回款");
        ownerReceiptRepository.save(r1);

        OwnerReceipt r2 = new OwnerReceipt();
        r2.setOwner(ownerB);
        r2.setPayDate(today);
        r2.setAmount(new BigDecimal("2000"));
        r2.setRemark("首付款");
        ownerReceiptRepository.save(r2);
    }

    private void seedAttendanceAndProduction() {
        List<Worker> workers = workerRepository.findAll();
        LocalDate today = LocalDate.now();
        for (Worker worker : workers) {
            Attendance attendance = new Attendance();
            attendance.setWorker(worker);
            attendance.setWorkDate(today);
            attendance.setStatus(AttendanceStatus.PRESENT);
            attendanceRepository.save(attendance);
        }

        Order order = orderRepository.findAll().stream().findFirst().orElse(null);
        if (order != null) {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            if (!items.isEmpty()) {
                OrderItem item = items.get(0);
                Worker defaultWorker = workers.isEmpty() ? null : workers.get(0);
                BigDecimal pieceCount = item.getPieceCount().min(new BigDecimal("60"));
                BigDecimal gross = pieceCount.multiply(item.getPieceWeight());
                BigDecimal net = gross.multiply(new BigDecimal("0.9"));

                ProductionRecord record = new ProductionRecord();
                record.setOrder(order);
                record.setOrderItem(item);
                if (defaultWorker != null) {
                    record.setWorker(defaultWorker);
                }
                record.setWorkDate(today);
                record.setGrossTonnage(gross);
                record.setNetTonnage(net);
                record.setPieceCount(pieceCount);
                productionRecordRepository.save(record);

                item.setDonePieceCount(item.getDonePieceCount().add(pieceCount));
                item.setDoneGrossTonnage(item.getDoneGrossTonnage().add(gross));
                item.setDoneNetTonnage(item.getDoneNetTonnage().add(net));
                orderItemRepository.save(item);

                order.setDoneGrossTonnage(order.getDoneGrossTonnage().add(gross));
                order.setDoneNetTonnage(order.getDoneNetTonnage().add(net));
                order.setDonePieceCount(order.getDonePieceCount().add(pieceCount));
                order.setProductionStatus(ProductionStatus.IN_PROGRESS);
                orderRepository.save(order);
            }
        }
    }
}
