package com.ddang.ddang.bid.application.exception;

public class InvalidBidderException extends IllegalArgumentException {

    public InvalidBidderException(final String message) {
        super(message);
    }
}
