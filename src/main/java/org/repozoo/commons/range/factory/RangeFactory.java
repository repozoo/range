package org.repozoo.commons.range.factory;

import org.repozoo.commons.range.Range;
import org.repozoo.commons.range.ValueIterator;
import org.repozoo.commons.range.value.Value;

import java.util.Comparator;
import java.util.function.UnaryOperator;

public class RangeFactory {

    public interface CreateRange<T> {
        Range<T> between(T from, T to);
    }

    @SuppressWarnings("unused")
    public static <Y> CreateRangeBuilder<Y> forType(Class<Y> rangeType) {
        //until now, rangeType is syntactic sugar only
        return new CreateRangeBuilder<>();
    }

    public static class CreateRangeBuilder<Y> {

        private Comparator<Y> comparator;
        private ValueIterator<Y> iterator;

        CreateRangeBuilder() {}

        public CreateRangeBuilder<Y> withIterator(UnaryOperator<Y> next, UnaryOperator<Y> previous) {
            this.iterator = new ValueIterator<>(next, previous);
            return this;
        }

        public CreateRangeBuilder<Y> withComparator(Comparator<Y> comparator) {
            this.comparator = comparator;
            return this;
        }

        public CreateRange<Y> build() {
            return (from, to) -> {
                Value<Y> start = Value.of(from, iterator, comparator);
                Value<Y> end = Value.of(to, iterator, comparator);
                return Range.between(start, end);
            };
        }
    }
}
