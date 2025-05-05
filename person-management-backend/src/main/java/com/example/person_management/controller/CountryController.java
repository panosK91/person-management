package com.example.person_management.controller;

import com.example.person_management.dto.CountryDTO;
import com.example.person_management.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    @GetMapping
    public ResponseEntity<List<CountryDTO>> listAll() {
        return ResponseEntity.ok(countryService.list());
    }
}
