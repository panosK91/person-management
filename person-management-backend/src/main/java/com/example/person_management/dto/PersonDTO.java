package com.example.person_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {
    private String name;
    private String surname;
    private LocalDate birthdate;
    private String email;
    private String phone;
    private Long addressId;
    private Set<Long> languageIds;
}

