package com.wa.exception;

public class ProcessNotFoundException extends RuntimeException {
    public ProcessNotFoundException(String message) {
        super(message);
    }
    
    public ProcessNotFoundException(Long id) {
        super("Processo não encontrado com ID: " + id);
    }
}

