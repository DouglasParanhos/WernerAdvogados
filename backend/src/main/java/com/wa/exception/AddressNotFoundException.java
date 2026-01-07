package com.wa.exception;

public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(String message) {
        super(message);
    }
    
    public AddressNotFoundException(Long id) {
        super("Endereço não encontrado com ID: " + id);
    }
}

