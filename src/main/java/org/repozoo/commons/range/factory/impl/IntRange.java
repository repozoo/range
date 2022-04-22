package org.repozoo.commons.range.factory.impl;

import org.repozoo.commons.range.RangeImpl;
import org.repozoo.commons.range.factory.RangeFactory;

import java.util.Comparator;
import java.util.function.UnaryOperator;

public class IntRange {

    private static final RangeFactory.CreateRange<Integer> createRange = createIntRange();

    public static RangeImpl<Integer> between(Integer min, Integer max) {
        return createRange.between(min, max);
    }

    public static RangeImpl<Integer> singleton(int i) {
        return createRange.between(i, i);
    }

    private static RangeFactory.CreateRange<Integer> createIntRange() {
        UnaryOperator<Integer> next = n -> n + 1;
        UnaryOperator<Integer> previous = n -> n - 1;
        return RangeFactory.forType(Integer.class)
                .withComparator(Comparator.naturalOrder())
                .withIterator(next, previous)
                .build();
    }
}
