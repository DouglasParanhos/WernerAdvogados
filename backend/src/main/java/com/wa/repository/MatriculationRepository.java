package com.wa.repository;

import com.wa.model.Matriculation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatriculationRepository extends JpaRepository<Matriculation, Long> {
    List<Matriculation> findByPersonId(Long personId);
    
    @Query("SELECT DISTINCT m FROM Matriculation m LEFT JOIN FETCH m.processes WHERE m.person.id = :personId")
    List<Matriculation> findByPersonIdWithProcesses(@Param("personId") Long personId);
    
    @Query("SELECT m FROM Matriculation m LEFT JOIN FETCH m.processes WHERE m.id = :id")
    Optional<Matriculation> findByIdWithProcesses(@Param("id") Long id);
}

