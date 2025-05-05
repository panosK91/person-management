package com.example.person_management.dto;

public record AddressResponseDTO(
        Long id,
        String street,
        String city,
        String state,
        String zipcode
) {}