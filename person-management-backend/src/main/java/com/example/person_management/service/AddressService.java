package com.example.person_management.service;

import com.example.person_management.dto.AddressRequestDTO;
import com.example.person_management.dto.AddressResponseDTO;
import com.example.person_management.entity.Address;
import com.example.person_management.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressResponseDTO create(AddressRequestDTO dto) {
        Address a = Address.builder()
                .street(dto.street())
                .city(dto.city())
                .state(dto.state())
                .zipcode(dto.zipcode())
                .build();
        Address saved = addressRepository.save(a);
        return toDTO(saved);
    }

    public List<AddressResponseDTO> list() {
        return addressRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public AddressResponseDTO getById(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Address not found with ID: " + id));
        return toDTO(address);
    }

    private AddressResponseDTO toDTO(Address a) {
        return new AddressResponseDTO(
                a.getId(),
                a.getStreet(),
                a.getCity(),
                a.getState(),
                a.getZipcode()
        );
    }

    public AddressResponseDTO update(Long id, AddressRequestDTO dto) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Address not found with ID: " + id));

        address.setStreet(dto.street());
        address.setCity(dto.city());
        address.setState(dto.state());
        address.setZipcode(dto.zipcode());

        Address updated = addressRepository.save(address);
        return toDTO(updated);
    }
}