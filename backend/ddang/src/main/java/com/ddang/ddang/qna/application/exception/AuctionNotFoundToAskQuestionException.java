package com.ddang.ddang.qna.application.exception;

public class AuctionNotFoundToAskQuestionException extends IllegalArgumentException {

    public AuctionNotFoundToAskQuestionException(final String message) {
        super(message);
    }
}
