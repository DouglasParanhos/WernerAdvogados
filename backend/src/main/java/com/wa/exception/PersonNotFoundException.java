package com.wa.exception;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException(String message) {
        super(message);
    }
    
    public PersonNotFoundException(Long id) {
        super("Cliente não encontrado com ID: " + id);
    }
}

