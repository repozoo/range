package org.repozoo.commons.range.factories;

import org.repozoo.commons.range.Range;
import org.repozoo.commons.range.RangeFactory;
import org.repozoo.commons.range.RangeI;

import java.time.LocalDate;
import java.util.function.UnaryOperator;

public class LocalDateRange {

    private LocalDateRange() {}

    private static final RangeFactory.CreateRange<LocalDate> createRange = createRange();

    public static RangeI<LocalDate> between(LocalDate min, LocalDate max) {
        return createRange.between(min, max);
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
