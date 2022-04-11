package org.repozoo.commons.range;

import org.repozoo.commons.range.value.Value;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Range<T> extends RangeSet<T> {

    Value<T> min();
    Value<T> max();

    default T from() {
        return min().value();
    }

    default T to() {
        return max().value();
    }

    @Override
    default boolean intersects(RangeSet<T> others) {
        return others.stream().anyMatch(other -> !isBefore(other) && !isAfter(other));
    }

    @Override
    default RangeSet<T> intersection(RangeSet<T> others) {
        return others.stream().map(other -> Range.intersection(this, other))
                .reduce(RangeSet.empty(), RangeSet::sum);
    }

    /**
     * True if this.to < other.from, false otherwise.
     */
    default boolean isBefore(Range<T> other) {
        return max().isBefore(other.min());
    }

    /**
     * True if this.from > other.to, false otherwise.
     */
    default boolean isAfter(Range<T> other) {
        return min().isAfter(other.max());
    }

    /**
     * Must always be true for a Range.
     */
    @Override
    default boolean isEmpty() {
        return false;
    }

    @Override
    default Stream<Range<T>> stream() {
        return Stream.of(this);
    }

    default boolean startsBefore(Range<T> other) {
        return min().isBefore(other.min());
    }

    default boolean endsAfter(Range<T> other) {
        return max().isAfter(other.max());
    }

    @SafeVarargs
    static <T> Range<T> enclose(Range<T>... ranges) {
        Objects.requireNonNull(ranges);
        Value<T> minStart = min(Range::min, ranges);
        Value<T> maxEnd = max(Range::max, ranges);
        return Range.between(minStart, maxEnd);
    }

    static <X> Range<X> between(Value<X> from, Value<X> to) {
        return RangeImpl.between(from, to);
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
