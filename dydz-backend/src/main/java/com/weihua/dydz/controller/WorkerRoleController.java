package com.weihua.dydz.controller;

import com.weihua.dydz.domain.WorkerRole;
import com.weihua.dydz.repository.WorkerRoleRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/worker-roles")
@RequiredArgsConstructor
public class WorkerRoleController {
    private final WorkerRoleRepository workerRoleRepository;

    @GetMapping
    public List<WorkerRole> list() {
        return workerRoleRepository.findAll();
    }

    @PostMapping
    public WorkerRole create(@Valid @RequestBody WorkerRole role) {
        role.setId(null);
        return workerRoleRepository.save(role);
    }

    @PutMapping("/{id}")
    public WorkerRole update(@PathVariable Long id, @Valid @RequestBody WorkerRole role) {
        role.setId(id);
        return workerRoleRepository.save(role);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        workerRoleRepository.deleteById(id);
    }
}
