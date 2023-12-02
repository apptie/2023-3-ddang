package com.ddang.ddang.auction.application.exception;

public class UninitializedRegionException extends IllegalArgumentException {

    public UninitializedRegionException(final String message) {
        super(message);
    }
}
