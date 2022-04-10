package org.repozoo.commons.range.factory;

import org.repozoo.commons.range.Range;

public class RangeFactory {

    public interface CreateRange<T> {
        Range<T> between(T from, T to);
    }

    @SuppressWarnings("unused")
    public static <Y> CreateRangeBuilder<Y> forType(Class<Y> rangeType) {
        //until now, rangeType is syntactic sugar only
        return new CreateRangeBuilder<>();
    }
}
