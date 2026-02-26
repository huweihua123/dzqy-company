package com.weihua.dydz.controller;

import com.weihua.dydz.domain.Owner;
import com.weihua.dydz.repository.OwnerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/owners")
@RequiredArgsConstructor
public class OwnerController {
    private final OwnerRepository ownerRepository;

    @GetMapping
    public List<Owner> list() {
        return ownerRepository.findAll();
    }

    @PostMapping
    public Owner create(@Valid @RequestBody Owner owner) {
        owner.setId(null);
        return ownerRepository.save(owner);
    }

    @PutMapping("/{id}")
    public Owner update(@PathVariable Long id, @Valid @RequestBody Owner owner) {
        owner.setId(id);
        return ownerRepository.save(owner);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        ownerRepository.deleteById(id);
    }
}
