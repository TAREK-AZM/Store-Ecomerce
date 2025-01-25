package org.store.api.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message); // Pass the message to the RuntimeException constructor
    }
}
