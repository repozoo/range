package org.repozoo.commons.range;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface RangeSet<T> {

    /**
     * Returns a {@link SimpleRange} stream containing all ranges of this set<br>
     * or an empty stream if this {@link RangeSet} is empty.
     */
    Stream<Range<T>> streamRanges();

    default Stream<T> streamValues() {
        return streamRanges().flatMap(Range::streamValues);
    }

    default void forEachValue(Consumer<T> valueConsumer) {
        streamValues().forEach(valueConsumer);
    }


    /**
     * Returns true if the following is true for each {@link SimpleRange} of others:<br>
     * At least one {@link SimpleRange} of this set contains 'other'.
     */
    default boolean contains(RangeSet<T> others) {
        return others.streamRanges().allMatch(other -> streamRanges().anyMatch(r -> r.contains(other)));
    }

    /**
     * Returns true if any {@link SimpleRange} of this set intersects with at least one {@link SimpleRange} of others.
     */
    default boolean intersects(RangeSet<T> others) {
        return streamRanges().anyMatch(aRange -> others.streamRanges().anyMatch(aRange::intersects));
    }

    /**
     * Returns a new RangeSet containing all {@link SimpleRange} parts that exist in this and others.
     */
    default RangeSet<T> intersection(RangeSet<T> others) {
        List<Range<T>> intersections = streamRanges()
            .map(others::intersection)
            .flatMap(RangeSet::streamRanges)
            .collect(Collectors.toList());
        return newRangeSet(intersections);
    }

    /**
     * Returns true if this {@link SimpleRange} and other do not intersect.
     */
    default boolean isDistinct(Range<T> other) {
        return !intersects(other);
    }

    /**
     * Returns a new {@link RangeSet} containing the sum of this and others ranges.<br>
     * Example:<br>
     * <ul>
     *     <li><pre>rs1([1-3],[7-10])</pre></li>
     *     <li><pre>rs2([4-5],[9-12])</pre></li>
     * </ul>
     * <pre>rs1.add(rs2) returns rs3([1-5],[7-12])</pre>
     */
    default RangeSet<T> add(RangeSet<T> others) {
        return RangeSet.mergeOverlappingAndAdjacent(this, others);
    }

    /**
     * Returns a new {@link RangeSet} subtracting others from this ranges.<br>
     * Example:<br>
     * <ul>
     *     <li><pre>rs1([1-5],[7-10])</pre></li>
     *     <li><pre>rs2([3-4],[9-12])</pre></li>
     * </ul>
     * <pre>rs1.remove(rs2) returns rs3([1-2],[5-5],[7-8])</pre>
     */
    default RangeSet<T> remove(RangeSet<T> others) {
        List<Range<T>> newRanges = new ArrayList<>();
        getRanges().forEach(range -> {
            Stack<Range<T>> stack = new Stack<>();
            stack.push(range);
            others.streamRanges().forEach(other -> {
                if (!stack.isEmpty()) {
                    Range<T> topRange = stack.pop();
                    RangeSet<T> result = SimpleRange.remove(topRange, other);
                    result.streamRanges().filter(Predicate.not(RangeSet::isEmpty)).forEach(stack::push);
                }
            });
            newRanges.addAll(stack);
        });
        return newRangeSet(newRanges);
    }

    /**
     * Returns true if this set has no range
     */
    default boolean isEmpty() {
        return getRanges().isEmpty();
    }

    /**
     * Returns a list of all {@link SimpleRange}s in this set, ordered by Range::min ascending.
     */
    default List<Range<T>> getRanges() {
        return streamRanges().collect(Collectors.toList());
    }


    /**
     * Creates a new {@link RangeSet} containing no ranges.
     */
    static <T> RangeSet<T> empty() {
        return Stream::empty;
    }

    /**
     * Creates a new {@link RangeSet} containing all specified {@link SimpleRange}s after being normalized.
     */
    @SafeVarargs
    static <T> RangeSet<T> of(Range<T>... ranges) {
        Objects.requireNonNull(ranges);
        return mergeOverlappingAndAdjacent(Arrays.stream(ranges));
    }

    /**
     * Alias for {@link RangeSet#mergeOverlappingAndAdjacent(RangeSet, RangeSet)}
     */
    static <T> RangeSet<T> sum(RangeSet<T> set1, RangeSet<T> set2) {
        return RangeSet.mergeOverlappingAndAdjacent(set1, set2);
    }

    static <T> RangeSet<T> mergeOverlappingAndAdjacent(RangeSet<T> set1, RangeSet<T> set2) {
        Stream<Range<T>> rangeStream = Stream
            .concat(
                set1.streamRanges(),
                set2.streamRanges()
            );
        return RangeSet.mergeOverlappingAndAdjacent(rangeStream);
    }

    /**
     * TODO
     */
    static <T> String toString(RangeSet<T> rangeSet) {
        return rangeSet.streamRanges().map(Range::toString).collect(Collectors.joining("\n"));
    }

    private static <T> RangeSet<T> mergeOverlappingAndAdjacent(Stream<Range<T>> rangeStream) {
        Stack<Range<T>> stackedRanges = rangeStream.sorted(Comparator.comparing(Range::minValue)).collect(RangeSet.toStack());
        return newRangeSet(stackedRanges);
    }

    private static <T> RangeSet<T> newRangeSet(Collection<Range<T>> rangeCollection) {
        return rangeCollection::stream;
    }

    @SafeVarargs
    private static <T> RangeSet<T> newRangeSet(Range<T>... ranges) {
        return Arrays.asList(ranges)::stream;
    }

    private static <T> Collector<Range<T>, Stack<Range<T>>, Stack<Range<T>>> toStack() {
        return Collector.of(
            Stack::new,
            RangeSet::addOnTop,
            RangeSet::combineStacks
        );
    }

    private static <T> void addOnTop(Stack<Range<T>> stack, Range<T> range) {
        if (stack.isEmpty()) {
            stack.push(range);
        } else {
            Range<T> topRange = stack.pop();
            RangeSet<T> sum;
            if (topRange.intersects(range) || topRange.maxValue().next().isEqualTo(range.minValue())) {
                sum = SimpleRange.newRangeFromGlobalMinMax(topRange, range);
            } else {
                sum = newRangeSet(topRange, range);
            }
            sum.getRanges().forEach(stack::push);
        }
    }

    private static <T> Stack<Range<T>> combineStacks(Stack<Range<T>> stack1, Stack<Range<T>> stack2) {
        throw new UnsupportedOperationException("TODO implement ...");
    }
}
