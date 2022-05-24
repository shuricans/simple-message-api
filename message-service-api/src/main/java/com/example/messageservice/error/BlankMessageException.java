package com.example.messageservice.error;

public class BlankMessageException extends RuntimeException {
    public BlankMessageException() {
        super();
    }

    public BlankMessageException(String message) {
        super(message);
    }
}
