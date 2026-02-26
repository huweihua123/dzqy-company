package com.weihua.dydz.controller;

import com.weihua.dydz.domain.Worker;
import com.weihua.dydz.domain.WorkerRole;
import com.weihua.dydz.domain.WorkerStatus;
import com.weihua.dydz.dto.WorkerRequest;
import com.weihua.dydz.repository.WorkerRoleRepository;
import com.weihua.dydz.repository.WorkerRepository;
import com.weihua.dydz.repository.WorkerGroupRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workers")
@RequiredArgsConstructor
public class WorkerController {
    private final WorkerRepository workerRepository;
    private final WorkerRoleRepository workerRoleRepository;
    private final WorkerGroupRepository workerGroupRepository;

    @GetMapping
    public List<Worker> list(@RequestParam(required = false) Long groupId) {
        if (groupId != null) {
            return workerRepository.findByGroupId(groupId);
        }
        return workerRepository.findAll();
    }

    @PostMapping
    public Worker create(@Valid @RequestBody WorkerRequest request) {
        WorkerRole role = workerRoleRepository.findById(request.roleId())
                .orElseThrow(() -> new IllegalArgumentException("Worker role not found"));
        Worker worker = new Worker();
        worker.setName(request.name());
        worker.setRole(role);
        worker.setPayType(request.payType());
        if (request.groupId() != null) {
            worker.setGroup(workerGroupRepository.findById(request.groupId())
                    .orElseThrow(() -> new IllegalArgumentException("Worker group not found")));
        }
        worker.setPhone(request.phone());
        worker.setStatus(request.status() != null ? request.status() : WorkerStatus.ACTIVE);
        worker.setTonPrice(request.tonPrice());
        worker.setDailySalary(request.dailySalary());
        worker.setYearlySalary(request.yearlySalary());
        worker.setRemark(request.remark());
        return workerRepository.save(worker);
    }

    @PutMapping("/{id}")
    public Worker update(@PathVariable Long id, @Valid @RequestBody WorkerRequest request) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Worker not found"));
        WorkerRole role = workerRoleRepository.findById(request.roleId())
                .orElseThrow(() -> new IllegalArgumentException("Worker role not found"));
        worker.setName(request.name());
        worker.setRole(role);
        worker.setPayType(request.payType());
        if (request.groupId() != null) {
            worker.setGroup(workerGroupRepository.findById(request.groupId())
                    .orElseThrow(() -> new IllegalArgumentException("Worker group not found")));
        } else {
            worker.setGroup(null);
        }
        worker.setPhone(request.phone());
        worker.setStatus(request.status() != null ? request.status() : worker.getStatus());
        worker.setTonPrice(request.tonPrice());
        worker.setDailySalary(request.dailySalary());
        worker.setYearlySalary(request.yearlySalary());
        worker.setRemark(request.remark());
        return workerRepository.save(worker);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        workerRepository.deleteById(id);
    }
}
