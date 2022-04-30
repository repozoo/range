package org.repozoo.commons.range;

import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

@EqualsAndHashCode
public class Range<T> implements RangeSet<T> {

    private final Value<T> min;
    private final Value<T> max;

    private Range(Value<T> min, Value<T> max) {
        Objects.requireNonNull(min);
        Objects.requireNonNull(max);
        if (min.isAfter(max)) {
            throw new IllegalArgumentException("min must not be after max, \nmin: " + min + "\nmax: " + max);
        }
        this.min = min;
        this.max = max;
    }

    Value<T> minValue() {
        return min;
    }

    Value<T> maxValue() {
        return max;
    }

    /**
     * Returns the inclusive start value of this range.
     */
    public T from() {
        return minValue().value();
    }

    /**
     * Returns the inclusive end value of this range.
     */
    public T to() {
        return maxValue().value();
    }

    /**
     * Returns true if t lies inside this range.
     */
    public boolean contains(T value) {
        return minValue().isBeforeOrEqual(value) && maxValue().isAfterOrEqual(value);
    }

    /**
     * Returns true if the other {@link Range} lies inside or is equal to this range.
     */
    public boolean contains(Range<T> other) {
        return contains(other.minValue()) && contains(other.maxValue());
    }

    /**
     * Returns true if any {@link Range} of others intersects with this {@link Range}.
     */
    @Override
    public boolean intersects(RangeSet<T> others) {
        return others.stream().anyMatch(this::intersects);
    }

    @Override
    public RangeSet<T> intersection(RangeSet<T> others) {
        return others.stream().map(other -> Range.intersection(this, other))
                .reduce(RangeSet.empty(), RangeSet::sum);
    }

    /**
     * Returns true if this range ends before the other starts.
     */
    public boolean isBefore(Range<T> other) {
        return maxValue().isBefore(other.minValue());
    }

    /**
     * Returns true if this range starts after the other ends.
     */
    public boolean isAfter(Range<T> other) {
        return minValue().isAfter(other.maxValue());
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Stream<Range<T>> stream() {
        return Stream.of(this);
    }

    /**
     * Returns true if this.min() is before other.min().
     */
    public boolean startsBefore(Range<T> other) {
        return minValue().isBefore(other.minValue());
    }

    /**
     * Returns true if this.max() is after other.max().
     */
    public boolean endsAfter(Range<T> other) {
        return maxValue().isAfter(other.maxValue());
    }

    boolean intersects(Range<T> other) {
        return this.contains(other.minValue()) || this.contains(other.maxValue()) || other.contains(this);
    }

    @Override
    public String toString() {
        return "Range{from=" + from() + ", to=" + to() + '}';
    }

    private boolean contains(Value<T> value) {
        return minValue().isBeforeOrEqual(value) && maxValue().isAfterOrEqual(value);
    }

    /**
     * Returns a {@link Range} that surrounds all ranges<br>
     * example: enclose([1-3], [5-8]) -> [1-8]
     */
    @SafeVarargs
    public static <T> Range<T> surround(Range<T>... ranges) {
        Objects.requireNonNull(ranges);
        Value<T> minStart = min(Range::minValue, ranges);
        Value<T> maxEnd = max(Range::maxValue, ranges);
        return Range.between(minStart, maxEnd);
    }

    static <X> Range<X> between(Value<X> min, Value<X> max) {
        return new Range<>(min, max);
    }

    static <T> RangeSet<T> remove(Range<T> aRange, Range<T> toRemove) {
        if (aRange.intersects(toRemove)) {
            if (aRange.equals(toRemove) || toRemove.contains(aRange)) {
                return RangeSet.empty();
            } else if (aRange.contains(toRemove) && (toRemove.minValue().isAfter(aRange.minValue()) && toRemove.maxValue().isBefore(aRange.maxValue()))) {
                Range<T> r1 = Range.between(aRange.minValue(), toRemove.minValue().previous());
                Range<T> r2 = Range.between(toRemove.maxValue().next(), aRange.maxValue());
                return RangeSet.of(r1, r2);
            } else {
                if (toRemove.minValue().isAfter(aRange.minValue())) {
                    return Range.between(aRange.minValue(), toRemove.minValue().previous());
                } else {
                    return Range.between(toRemove.maxValue().next(), aRange.maxValue());
                }
            }
        } else {
            return aRange;
        }
    }

    private static <T> RangeSet<T> intersection(Range<T> aRange, Range<T> other) {
        if (aRange.intersects(other)) {
            Value<T> maxStart = max(Range::minValue, aRange, other);
            Value<T> minEnd = min(Range::maxValue, aRange, other);
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
