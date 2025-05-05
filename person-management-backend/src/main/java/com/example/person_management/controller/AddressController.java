package com.example.person_management.controller;

import com.example.person_management.dto.AddressRequestDTO;
import com.example.person_management.dto.AddressResponseDTO;
import com.example.person_management.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@CrossOrigin
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressResponseDTO> create(@RequestBody AddressRequestDTO dto) {
        return ResponseEntity.ok(addressService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> list() {
        return ResponseEntity.ok(addressService.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> update(
            @PathVariable Long id,
            @RequestBody AddressRequestDTO dto
    ) {
        return ResponseEntity.ok(addressService.update(id, dto));
    }
}
