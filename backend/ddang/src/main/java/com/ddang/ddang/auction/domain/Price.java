package com.ddang.ddang.auction.domain;

import com.ddang.ddang.auction.domain.exception.InvalidPriceValueException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
@ToString
public class Price {

    private static final int MINIMUM_PRICE = 0;
    private static final int MAXIMUM_PRICE = 2_100_000_000;

    private static final String EXCEPTION_MESSAGE = "가격은 %d원 이상, %d원 이하여야 합니다.";

    private int value;

    public Price(final int value) {
        if (value < MINIMUM_PRICE || value > MAXIMUM_PRICE) {
            throw new InvalidPriceValueException(String.format(EXCEPTION_MESSAGE, MINIMUM_PRICE, MAXIMUM_PRICE));
        }

        this.value = value;
    }
}
