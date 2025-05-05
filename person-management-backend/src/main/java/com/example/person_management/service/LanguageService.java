package com.example.person_management.service;

import com.example.person_management.dto.LanguageDTO;
import com.example.person_management.repository.LanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LanguageService {

    private final LanguageRepository languageRepository;

    public List<LanguageDTO> getAll() {
        return languageRepository.findAll().stream()
                .map(l -> new LanguageDTO(l.getId(), l.getName(), l.getIsoCode()))
                .toList();
    }
}
