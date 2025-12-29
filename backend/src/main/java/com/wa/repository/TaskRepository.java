package com.wa.repository;

import com.wa.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatusOrderByOrdemAsc(String status);
    List<Task> findByResponsavelOrderByOrdemAsc(String responsavel);
    List<Task> findAllByOrderByStatusAscOrdemAsc();
}




