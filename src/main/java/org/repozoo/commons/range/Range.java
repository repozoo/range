package org.repozoo.commons.range;

import org.repozoo.commons.range.value.Value;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Range<T> extends RangeSet<T> {

    /**
     * Returns the minimal (inclusive) value of this {@link Range}
     */
    Value<T> min();
    /**
     * Returns the maximal (inclusive) value of this {@link Range}
     */
    Value<T> max();

    /**
     * Returns the inclusive start value of this range.
     */
    default T from() {
        return min().value();
    }

    /**
     * Returns the inclusive end value of this range.
     */
    default T to() {
        return max().value();
    }

    /**
     * Returns true if t lies inside this range.
     */
    default boolean contains(T t) {
        return min().isBeforeOrEqual(t) && max().isAfterOrEqual(t);
    }

    /**
     * Returns true if the other {@link Range} lies inside or is equal to this range.
     */
    default boolean contains(Range<T> other) {
        return contains(other.min()) && contains(other.max());
    }

    /**
     * Returns true if any {@link Range} of others intersects with this {@link Range}.
     */
    @Override
    default boolean intersects(RangeSet<T> others) {
        return others.stream().anyMatch(this::intersects);
    }

    @Override
    default RangeSet<T> intersection(RangeSet<T> others) {
        return others.stream().map(other -> Range.intersection(this, other))
                .reduce(RangeSet.empty(), RangeSet::sum);
    }

    /**
     * Returns true if this range ends before the other starts.
     */
    default boolean isBefore(Range<T> other) {
        return max().isBefore(other.min());
    }

    /**
     * Returns true if this range starts after the other ends.
     */
    default boolean isAfter(Range<T> other) {
        return min().isAfter(other.max());
    }

    @Override
    default boolean isEmpty() {
        return false;
    }

    @Override
    default Stream<Range<T>> stream() {
        return Stream.of(this);
    }

    /**
     * Returns true if this.min() is before other.min().
     */
    default boolean startsBefore(Range<T> other) {
        return min().isBefore(other.min());
    }

    /**
     * Returns true if this.max() is after other.max().
     */
    default boolean endsAfter(Range<T> other) {
        return max().isAfter(other.max());
    }

    private boolean contains(Value<T> value) {
        return min().isBeforeOrEqual(value) && max().isAfterOrEqual(value);
    }

    private boolean intersects(Range<T> other) {
        return this.contains(other.min()) || this.contains(other.max()) || other.contains(this);
    }

    /**
     * Returns a {@link Range} that surrounds all ranges<br>
     * example: enclose([1-3], [5-8]) -> [1-8]
     */
    @SafeVarargs
    static <T> Range<T> sourround(Range<T>... ranges) {
        Objects.requireNonNull(ranges);
        Value<T> minStart = min(Range::min, ranges);
        Value<T> maxEnd = max(Range::max, ranges);
        return Range.between(minStart, maxEnd);
    }

    /**
     * Returns a new {@link Range} between min (inclusive) and max (inclusive).
     */
    static <X> Range<X> between(Value<X> min, Value<X> max) {
        return RangeImpl.between(min, max);
    }

    private static <T> RangeSet<T> intersection(Range<T> aRange, Range<T> other) {
        if (aRange.intersects(other)) {
            Value<T> maxStart = max(Range::min, aRange, other);
            Value<T> minEnd = min(Range::max, aRange, other);
            return Range.between(maxStart, minEnd);
        } else {
            return RangeSet.empty();
        }
    }

    @SafeVarargs
    private static <T> Value<T> min(Function<Range<T>, Value<T>> extraction, Range<T>... ranges) {
        return Arrays.stream(ranges).min(Comparator.comparing(extraction)).map(extraction).orElseThrow();
    }

    @SafeVarargs
    private static <T> Value<T> max(Function<Range<T>, Value<T>> extraction, Range<T>... ranges) {
        return Arrays.stream(ranges).max(Comparator.comparing(extraction)).map(extraction).orElseThrow();
    }
}
