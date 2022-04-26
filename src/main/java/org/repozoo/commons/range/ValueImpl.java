package org.repozoo.commons.range;

import lombok.*;

import java.util.Comparator;

@ToString
@AllArgsConstructor
@EqualsAndHashCode
@With(AccessLevel.PRIVATE)
class ValueImpl<T> implements Value<T> {

    T value;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    ValueIterator<T> iterator;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Comparator<T> comparator;

    public T value() {
        return value;
    }

    public Value<T> next() {
        return withValue(iterator.next(value));
    }

    public Value<T> previous() {
        return withValue(iterator.previous(value));
    }

    @Override
    public Value<T> with(T value) {
        return withValue(value);
    }

    @Override
    public int compareTo(Value<T> other) {
        return comparator.compare(this.value(), other.value());
    }
}
