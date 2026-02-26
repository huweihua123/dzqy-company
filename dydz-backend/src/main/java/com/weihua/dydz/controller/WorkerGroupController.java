package com.weihua.dydz.controller;

import com.weihua.dydz.domain.WorkerGroup;
import com.weihua.dydz.repository.WorkerGroupRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/worker-groups")
@RequiredArgsConstructor
public class WorkerGroupController {
    private final WorkerGroupRepository workerGroupRepository;

    @GetMapping
    public List<WorkerGroup> list() {
        return workerGroupRepository.findAll();
    }

    @PostMapping
    public WorkerGroup create(@Valid @RequestBody WorkerGroup group) {
        group.setId(null);
        return workerGroupRepository.save(group);
    }

    @PutMapping("/{id}")
    public WorkerGroup update(@PathVariable Long id, @Valid @RequestBody WorkerGroup group) {
        group.setId(id);
        return workerGroupRepository.save(group);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        workerGroupRepository.deleteById(id);
    }
}
