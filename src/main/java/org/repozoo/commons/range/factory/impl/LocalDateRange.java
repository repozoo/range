package org.repozoo.commons.range.factory.impl;

import org.repozoo.commons.range.Range;
import org.repozoo.commons.range.factory.RangeFactory;

import java.time.LocalDate;
import java.util.function.UnaryOperator;

public class LocalDateRange {

    public static Range<LocalDate> between(LocalDate from, LocalDate to) {
        UnaryOperator<LocalDate> next = n -> n.plusDays(1);
        UnaryOperator<LocalDate> previous = n ->  n.minusDays(1);
        RangeFactory.CreateRange<LocalDate> createRange = RangeFactory.forType(LocalDate.class)
                .withComparator(LocalDate::compareTo)
                .withIterator(next, previous)
                .build();
        return createRange.between(from, to);
    }
}
