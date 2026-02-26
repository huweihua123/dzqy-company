package com.weihua.dydz.controller;

import com.weihua.dydz.domain.Material;
import com.weihua.dydz.repository.MaterialRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/materials")
@RequiredArgsConstructor
public class MaterialController {
    private final MaterialRepository materialRepository;

    @GetMapping
    public List<Material> list() {
        return materialRepository.findAll();
    }

    @PostMapping
    public Material create(@Valid @RequestBody Material material) {
        material.setId(null);
        return materialRepository.save(material);
    }

    @PutMapping("/{id}")
    public Material update(@PathVariable Long id, @Valid @RequestBody Material material) {
        material.setId(id);
        return materialRepository.save(material);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        materialRepository.deleteById(id);
    }
}
