package com.example.exeptions;

public class IncorrectRequestException extends Exception {
    public IncorrectRequestException(String msg) {
        super(msg);
    }
    public IncorrectRequestException() {

    }
}
