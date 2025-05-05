package com.example.person_management.dto;
import java.time.LocalDate;
import java.util.List;

public record PersonResponseDTO(
        Long id,
        String name,
        String surname,
        String email,
        String phone,
        LocalDate birthdate,
        String address,
        Long addressId,
        String country,
        Long countryId,
        List<String> languages
) {}
