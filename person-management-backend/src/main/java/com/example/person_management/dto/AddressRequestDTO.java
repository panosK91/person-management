package com.example.person_management.dto;

public record AddressRequestDTO(
        String street,
        String city,
        String state,
        String zipcode
) {}
