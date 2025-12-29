package com.wa.controller;

import com.wa.dto.TaskDTO;
import com.wa.dto.TaskRequestDTO;
import com.wa.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAll() {
        return ResponseEntity.ok(taskService.getAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(taskService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<TaskDTO> create(@RequestBody TaskRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(taskService.create(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> update(@PathVariable Long id, @RequestBody TaskRequestDTO request) {
        try {
            return ResponseEntity.ok(taskService.update(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            taskService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/reorder")
    public ResponseEntity<List<TaskDTO>> updateOrder(@RequestBody List<TaskRequestDTO> tasks) {
        try {
            return ResponseEntity.ok(taskService.updateOrder(tasks));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

