package com.heng.exception;

public class CycleReferenceException extends Exception {
    public CycleReferenceException(String message) {
        super(message);
    }
}
