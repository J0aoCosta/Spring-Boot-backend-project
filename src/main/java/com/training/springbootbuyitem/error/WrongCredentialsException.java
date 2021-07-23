package com.training.springbootbuyitem.error;

public class WrongCredentialsException extends RuntimeException{
    public WrongCredentialsException() {
        this("wrong credentials");
    }
    private WrongCredentialsException(String message) {
        super(message);
    }
}
