package org.repozoo.commons.range;

import java.util.stream.Stream;

public interface RangeI<T> extends RangeSet<T> {
    Value<T> minValue();

    Value<T> maxValue();

    T min();

    T max();

    default boolean contains(RangeI<T> other) {
        return contains(other.minValue()) && contains(other.maxValue());
    }

    default boolean contains(Value<T> value) {
        return minValue().isBeforeOrEqual(value) && maxValue().isAfterOrEqual(value);
    }
    
    default boolean contains(T t) {
        return minValue().isBeforeOrEqual(t) && maxValue().isAfterOrEqual(t);
    }

    /**
     * Returns true if any {@link Range} of others intersects with this {@link Range}.
     */
    default boolean intersects(RangeSet<T> others) {
        return others.streamRanges().anyMatch(this::intersects);
    }

    default boolean intersects(RangeI<T> other) {
        return this.contains(other.minValue()) || this.contains(other.maxValue()) || other.contains(this);
    }

    default RangeSet<T> intersection(RangeSet<T> others) {
        return others.streamRanges()
            .map(other -> Range.intersection(this, other))
            .reduce(RangeSet.empty(), RangeSet::mergeOverlappingAndAdjacent);
    }

    boolean isBefore(RangeI<T> other);

    boolean isAfter(RangeI<T> other);

    @Override boolean isEmpty();

    @Override Stream<RangeI<T>> streamRanges();

    @Override Stream<T> streamValues();

    boolean startsBefore(RangeI<T> other);
}
