package com.wa.repository;

import com.wa.model.Moviment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimentRepository extends JpaRepository<Moviment, Long> {
    @Query("SELECT m FROM Moviment m WHERE m.process.id = :processId ORDER BY m.date DESC")
    List<Moviment> findByProcessId(@Param("processId") Long processId);
}

