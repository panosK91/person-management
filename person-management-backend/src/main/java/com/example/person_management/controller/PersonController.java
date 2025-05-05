package com.example.person_management.controller;


import com.example.person_management.dto.PersonRequestDTO;
import com.example.person_management.dto.PersonResponseDTO;
import com.example.person_management.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
@CrossOrigin
public class PersonController {

    private final PersonService personService;

    @PostMapping
    public ResponseEntity<PersonResponseDTO> create(@RequestBody PersonRequestDTO dto) {
        return ResponseEntity.ok(personService.createPerson(dto));
    }

    @GetMapping
    public ResponseEntity<List<PersonResponseDTO>> getAll() {
        return ResponseEntity.ok(personService.getAllPersons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonResponseDTO> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(personService.getPersonById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonResponseDTO> update(
            @PathVariable Long id,
            @RequestBody PersonRequestDTO dto
    ) {
        return ResponseEntity.ok(personService.updatePerson(id, dto));
    }
}

