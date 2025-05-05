package com.example.person_management.service;

import com.example.person_management.dto.PersonRequestDTO;
import com.example.person_management.dto.PersonResponseDTO;
import com.example.person_management.entity.Address;
import com.example.person_management.entity.Country;
import com.example.person_management.entity.Language;
import com.example.person_management.entity.Person;
import com.example.person_management.repository.AddressRepository;
import com.example.person_management.repository.CountryRepository;
import com.example.person_management.repository.LanguageRepository;
import com.example.person_management.repository.PersonRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final AddressRepository addressRepository;
    private final LanguageRepository languageRepository;
    private final CountryRepository countryRepository;

    public PersonResponseDTO createPerson(PersonRequestDTO dto) {
        Address address = addressRepository.findById(dto.addressId())
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        Set<Language> languages = new HashSet<>(languageRepository.findAllById(dto.languageIds()));
        Country country = countryRepository.findById(dto.countryId())
                .orElseThrow(() -> new IllegalArgumentException("Country not found"));

        Person person = Person.builder()
                .name(dto.name())
                .surname(dto.surname())
                .email(dto.email())
                .phone(dto.phone())
                .birthdate(LocalDate.parse(dto.birthdate()))
                .address(address)
                .country(country)
                .languages(languages)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Person saved = personRepository.save(person);
        return toDTO(saved);
    }

    @Transactional
    public List<PersonResponseDTO> getAllPersons() {
        return personRepository.findAllWithDetails().stream().map(this::toDTO).toList();
    }

    @Transactional
    public PersonResponseDTO getPersonById(Long id) {
        return personRepository.findByIdWithDetails(id)
                .map(this::toDTO)
                .orElseThrow(() -> new NoSuchElementException("Person not found"));
    }

    private PersonResponseDTO toDTO(Person p) {
        return new PersonResponseDTO(
                p.getId(),
                p.getName(),
                p.getSurname(),
                p.getEmail(),
                p.getPhone(),
                p.getBirthdate(),
                formatAddress(p.getAddress()),
                p.getAddress().getId(),
                p.getCountry().getName(),
                p.getCountry().getId(),
                p.getLanguages().stream().map(Language::getName).toList()
        );
    }

    private String formatAddress(Address a) {
        return a.getStreet() + ", " + a.getCity() + ", " + a.getState() + " " + a.getZipcode();
    }

    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }

    @Transactional
    public PersonResponseDTO updatePerson(Long id, PersonRequestDTO dto) {
        Person person = personRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new NoSuchElementException("Person not found"));


        Set<Language> languages = new HashSet<>(languageRepository.findAllById(dto.languageIds()));

        Country country = countryRepository.findById(dto.countryId())
                .orElseThrow(() -> new IllegalArgumentException("Country not found"));

        person.setName(dto.name());
        person.setSurname(dto.surname());
        person.setEmail(dto.email());
        person.setPhone(dto.phone());
        person.setBirthdate(LocalDate.parse(dto.birthdate()));
        person.setLanguages(languages);
        person.setUpdatedAt(LocalDateTime.now());
        person.setCountry(country);
        Person updated = personRepository.save(person);
        return toDTO(updated);
    }
}