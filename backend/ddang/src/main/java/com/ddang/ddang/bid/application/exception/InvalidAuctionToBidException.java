package com.ddang.ddang.bid.application.exception;

public class InvalidAuctionToBidException extends IllegalArgumentException {

    public InvalidAuctionToBidException(final String message) {
        super(message);
    }
}
