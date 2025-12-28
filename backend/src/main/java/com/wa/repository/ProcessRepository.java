package com.wa.repository;

import com.wa.model.Process;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessRepository extends JpaRepository<Process, Long> {
    List<Process> findByMatriculationId(Long matriculationId);
    
    @Query("SELECT p FROM Process p WHERE p.matriculation.person.id = :personId")
    List<Process> findByPersonId(@Param("personId") Long personId);
    
    @Query("SELECT p FROM Process p LEFT JOIN FETCH p.moviments WHERE p.id = :id")
    Optional<Process> findByIdWithMoviments(@Param("id") Long id);
    
    @Query("SELECT DISTINCT p FROM Process p " +
           "LEFT JOIN FETCH p.matriculation m " +
           "LEFT JOIN FETCH m.person per " +
           "LEFT JOIN FETCH per.address " +
           "WHERE p.id = :id")
    Optional<Process> findByIdWithRelations(@Param("id") Long id);
}

