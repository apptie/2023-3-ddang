package com.ddang.ddang.auction.domain;

import com.ddang.ddang.auction.domain.exception.InvalidBidUnitException;
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
public class BidUnit {

    private static final int MINIMUM_BID_UNIT = 0;
    private static final int MAXIMUM_BID_UNIT = 2_100_000_000;
    private static final int AVAILABLE_BID_UNIT = 100;

    private static final String EXCEPTION_MESSAGE = "입찰 단위는 %d원 이상, %d원 이하의 %d원 단위여야 합니다.";

    private int value;

    public BidUnit(final int value) {
        if (value < MINIMUM_BID_UNIT || value > MAXIMUM_BID_UNIT || value % AVAILABLE_BID_UNIT != 0) {
            throw new InvalidBidUnitException(
                    String.format(EXCEPTION_MESSAGE, MINIMUM_BID_UNIT, MAXIMUM_BID_UNIT, AVAILABLE_BID_UNIT)
            );
        }

        this.value = value;
    }
}
