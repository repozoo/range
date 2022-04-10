package org.repozoo.commons.range.factory.impl;

import org.repozoo.commons.range.Range;
import org.repozoo.commons.range.factory.RangeFactory;

import java.time.YearMonth;
import java.util.function.UnaryOperator;

public class YearMonthRange {

    public static Range<YearMonth> between(YearMonth from, YearMonth to) {
        UnaryOperator<YearMonth> next = n -> n.plusMonths(1);
        UnaryOperator<YearMonth> previous = n ->  n.minusMonths(1);
        RangeFactory.CreateRange<YearMonth> createRange = RangeFactory.forType(YearMonth.class)
                .withComparator(YearMonth::compareTo)
                .withIterator(next, previous)
                .build();
        return createRange.between(from, to);
    }
}
