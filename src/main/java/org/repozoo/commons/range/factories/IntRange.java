package org.repozoo.commons.range.factories;

import org.repozoo.commons.range.Range;
import org.repozoo.commons.range.RangeFactory;
import org.repozoo.commons.range.RangeI;

import java.util.Comparator;
import java.util.function.UnaryOperator;

public class IntRange {

    private IntRange() {}

    private static final RangeFactory.CreateRange<Integer> createRange = createIntRange();

    public static RangeI<Integer> between(Integer min, Integer max) {
        return createRange.between(min, max);
    }

    public static RangeI<Integer> singleton(int i) {
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
