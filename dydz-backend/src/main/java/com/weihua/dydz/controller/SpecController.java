package com.weihua.dydz.controller;

import com.weihua.dydz.domain.Spec;
import com.weihua.dydz.repository.SpecRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/specs")
@RequiredArgsConstructor
public class SpecController {
    private final SpecRepository specRepository;

    @GetMapping
    public List<Spec> list() {
        return specRepository.findAll();
    }

    @PostMapping
    public Spec create(@Valid @RequestBody Spec spec) {
        spec.setId(null);
        return specRepository.save(spec);
    }

    @PutMapping("/{id}")
    public Spec update(@PathVariable Long id, @Valid @RequestBody Spec spec) {
        spec.setId(id);
        return specRepository.save(spec);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        specRepository.deleteById(id);
    }
}
