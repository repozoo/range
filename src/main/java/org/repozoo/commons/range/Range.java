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
     * Returns the inclusive minimum of this range.
     */
    public T min() {
        return minValue().value();
    }

    /**
     * Returns the inclusive maximum of this range.
     */
    public T max() {
        return maxValue().value();
    }

    /**
     * Returns true if t lies inside this range.
     */
    public boolean contains(T t) {
        return minValue().isBeforeOrEqual(t) && maxValue().isAfterOrEqual(t);
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
        return others.streamRanges().anyMatch(this::intersects);
    }

    @Override
    public RangeSet<T> intersection(RangeSet<T> others) {
        return others.streamRanges()
            .map(other -> Range.intersection(this, other))
            .reduce(RangeSet.empty(), RangeSet::mergeOverlappingAndAdjacent);
    }

    /**
     * Returns true if this.max < other.min.
     */
    public boolean isBefore(Range<T> other) {
        return maxValue().isBefore(other.minValue());
    }

    /**
     * Returns true if this.min > other.max.
     */
    public boolean isAfter(Range<T> other) {
        return minValue().isAfter(other.maxValue());
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    /**
     * Returns a single element stream containing this range.
     */
    @Override
    public Stream<Range<T>> streamRanges() {
        return Stream.of(this);
    }

    @Override
    public Stream<T> streamValues() {
        return Stream
            .iterate(minValue(), value -> value.isBeforeOrEqual(maxValue()), Value::next)
            .map(Value::value);
    }

    /**
     * Returns true if this.min < other.min.
     */
    public boolean startsBefore(Range<T> other) {
        return minValue().isBefore(other.minValue());
    }

    /**
     * Returns true if this.max > other.max.
     */
    public boolean endsAfter(Range<T> other) {
        return maxValue().isAfter(other.maxValue());
    }

    boolean intersects(Range<T> other) {
        return this.contains(other.minValue()) || this.contains(other.maxValue()) || other.contains(this);
    }

    /**
     * Returns a String representation of this range in the form <pre>"Range[from=1, to=3]"</pre>
     */
    @Override
    public String toString() {
        return "Range{from=" + min() + ", to=" + max() + '}';
    }

    private boolean contains(Value<T> value) {
        return minValue().isBeforeOrEqual(value) && maxValue().isAfterOrEqual(value);
    }

    /**
     * Returns a {@link Range} with the global min max values of all supplied ranges<br>
     * example:
     * <ul>
     *     <li><pre>enclose([1-3], [5-8]) -> [1-8]</pre></li>
     *     <li><pre>enclose([1-6], [5-8]) -> [1-8]</pre></li>
     * </ul>
     */
    @SafeVarargs
    public static <T> Range<T> newRangeFromGlobalMinMax(Range<T>... ranges) {
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
