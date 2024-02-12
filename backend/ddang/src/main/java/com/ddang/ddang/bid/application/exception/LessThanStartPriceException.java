package com.ddang.ddang.bid.application.exception;

public class LessThanStartPriceException extends IllegalArgumentException {

    public LessThanStartPriceException(String message) {
        super(message);
    }
}
