package com.ddang.ddang.bid.application.exception;

public class LessThanPreviousBidException extends IllegalArgumentException {

    public LessThanPreviousBidException(String message) {
        super(message);
    }
}
