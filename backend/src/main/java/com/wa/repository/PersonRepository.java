package com.wa.repository;

import com.wa.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    @Query("SELECT DISTINCT p FROM Person p LEFT JOIN FETCH p.address LEFT JOIN FETCH p.matriculations")
    List<Person> findAllWithRelations();
    
    @Query("SELECT DISTINCT p FROM Person p LEFT JOIN FETCH p.address LEFT JOIN FETCH p.matriculations WHERE p.id = :id")
    Optional<Person> findByIdWithRelations(@Param("id") Long id);
}

