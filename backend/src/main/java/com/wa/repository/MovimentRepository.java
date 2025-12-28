package com.wa.repository;

import com.wa.model.Moviment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimentRepository extends JpaRepository<Moviment, Long> {
    List<Moviment> findByProcessId(Long processId);
}

