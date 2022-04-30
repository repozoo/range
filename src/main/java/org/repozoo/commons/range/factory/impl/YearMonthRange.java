package org.repozoo.commons.range.factory.impl;

import org.repozoo.commons.range.Range;
import org.repozoo.commons.range.factory.RangeFactory;

import java.time.YearMonth;
import java.util.function.UnaryOperator;

public class YearMonthRange {

    private YearMonthRange() {}

    private static final RangeFactory.CreateRange<YearMonth> createRange = createRange();

    public static Range<YearMonth> between(YearMonth min, YearMonth max) {
        return createRange.between(min, max);
    }

    private static RangeFactory.CreateRange<YearMonth> createRange() {
        UnaryOperator<YearMonth> next = n -> n.plusMonths(1);
        UnaryOperator<YearMonth> previous = n ->  n.minusMonths(1);
        return RangeFactory.forType(YearMonth.class)
                .withComparator(YearMonth::compareTo)
                .withIterator(next, previous)
                .build();
    }
}
