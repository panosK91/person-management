package com.example.person_management.service;

import com.example.person_management.dto.CountryDTO;
import com.example.person_management.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public List<CountryDTO> list() {
        return countryRepository.findAll().stream()
                .map(c -> new CountryDTO(c.getId(), c.getName(), c.getIsoCode()))
                .toList();
    }
}