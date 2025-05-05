package com.example.person_management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Language {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "iso_code")
    private String isoCode;

    @ManyToMany(mappedBy = "languages", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Person> persons;

    @Override
    public String toString() {
        return name;
    }
}

