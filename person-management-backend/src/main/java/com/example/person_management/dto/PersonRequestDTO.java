package com.example.person_management.dto;

import java.util.List;

public record PersonRequestDTO(
        String name,
        String surname,
        String birthdate,
        String email,
        String phone,
        Long addressId,
        Long countryId,
        List<Long> languageIds
) {}
