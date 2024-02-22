package com.ddang.ddang.bid.application.exception;

public class InvalidBidPriceException extends IllegalArgumentException {

    public InvalidBidPriceException(final String message) {
        super(message);
    }
}
