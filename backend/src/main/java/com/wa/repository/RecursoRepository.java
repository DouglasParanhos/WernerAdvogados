package com.wa.repository;

import com.wa.model.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long> {

    @Query("SELECT r FROM Recurso r WHERE r.process.id = :processId ORDER BY r.createdOn ASC")
    List<Recurso> findByProcessId(@Param("processId") Long processId);

    @Query("SELECT r FROM Recurso r WHERE r.process.id = :processId AND r.baixado = false ORDER BY r.createdOn ASC")
    List<Recurso> findByProcessIdAndBaixadoFalse(@Param("processId") Long processId);
}
