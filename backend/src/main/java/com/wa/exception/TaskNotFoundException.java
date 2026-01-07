package com.wa.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String message) {
        super(message);
    }
    
    public TaskNotFoundException(Long id) {
        super("Tarefa não encontrada com ID: " + id);
    }
}

