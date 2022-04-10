package org.repozoo.commons.range.factory.impl;

import org.repozoo.commons.range.Range;
import org.repozoo.commons.range.factory.RangeFactory;

import java.time.LocalDate;
import java.util.function.UnaryOperator;

public class LocalDateRange {

    private static final RangeFactory.CreateRange<LocalDate> createRange = createRange();

    public static Range<LocalDate> between(LocalDate from, LocalDate to) {
        RangeFactory.CreateRange<LocalDate> createRange = createRange();
        return createRange.between(from, to);
    }

    private static RangeFactory.CreateRange<LocalDate> createRange() {
        UnaryOperator<LocalDate> next = n -> n.plusDays(1);
        UnaryOperator<LocalDate> previous = n ->  n.minusDays(1);
        return RangeFactory.forType(LocalDate.class)
                .withComparator(LocalDate::compareTo)
                .withIterator(next, previous)
                .build();
    }
}
