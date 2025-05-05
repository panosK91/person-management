package com.example.person_management.repository;

import com.example.person_management.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
@Query("SELECT DISTINCT p FROM Person p " +
        "LEFT JOIN FETCH p.address " +
        "LEFT JOIN FETCH p.country " +
        "LEFT JOIN FETCH p.languages")
List<Person> findAllWithDetails();

    @Query("SELECT p FROM Person p " +
            "LEFT JOIN FETCH p.address " +
            "LEFT JOIN FETCH p.country " +
            "LEFT JOIN FETCH p.languages " +
            "WHERE p.id = :id")
    Optional<Person> findByIdWithDetails(@Param("id") Long id);
}
