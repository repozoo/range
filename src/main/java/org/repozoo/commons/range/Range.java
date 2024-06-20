package org.repozoo.commons.range;

import java.util.stream.Stream;

public interface Range<T> extends RangeSet<T> {
    Value<T> minValue();

    Value<T> maxValue();

    T min();

    T max();

    default boolean contains(Range<T> other) {
        return contains(other.minValue()) && contains(other.maxValue());
    }

    default boolean contains(Value<T> value) {
        return minValue().isBeforeOrEqual(value) && maxValue().isAfterOrEqual(value);
    }
    
    default boolean contains(T t) {
        return minValue().isBeforeOrEqual(t) && maxValue().isAfterOrEqual(t);
    }

    /**
     * Returns true if any {@link SimpleRange} of others intersects with this {@link SimpleRange}.
     */
    default boolean intersects(RangeSet<T> others) {
        return others.streamRanges().anyMatch(this::intersects);
    }

    default boolean intersects(Range<T> other) {
        return this.contains(other.minValue()) || this.contains(other.maxValue()) || other.contains(this);
    }

    default RangeSet<T> intersection(RangeSet<T> others) {
        return others.streamRanges()
            .map(other -> SimpleRange.intersection(this, other))
            .reduce(RangeSet.empty(), RangeSet::mergeOverlappingAndAdjacent);
    }

    boolean isBefore(Range<T> other);

    boolean isAfter(Range<T> other);

    @Override boolean isEmpty();

    @Override Stream<Range<T>> streamRanges();

    @Override Stream<T> streamValues();

    boolean startsBefore(Range<T> other);
}
