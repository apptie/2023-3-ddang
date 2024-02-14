package com.ddang.ddang.chat.application.exception;

public class UnavailableChatException extends IllegalArgumentException {

    public UnavailableChatException(final String message) {
        super(message);
    }
}
