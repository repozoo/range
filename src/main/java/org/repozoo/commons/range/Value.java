package org.repozoo.commons.range;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.Comparator;

@EqualsAndHashCode
@AllArgsConstructor
class Value<X> implements Comparable<Value<X>> {

    private final X value;
    private final ValueIterator<X> iterator;
    private final Comparator<X> comparator;

    @Override
    public int compareTo(Value<X> other) {
        return comparator.compare(value, other.value());
    }

    public X value() {
        return value;
    }

    public Value<X> next() {
        return with(iterator.next(value));
    }

    public Value<X> previous() {
        return with(iterator.previous(value));
    }

    public Value<X> with(X value) {
        return new Value<>(value, iterator, comparator);
    }

    public boolean isAfter(Value<X> other) {
        return this.compareTo(other) > 0;
    }

    public boolean isAfterOrEqual(Value<X> other) {
        int i = this.compareTo(other);
        return i > 0 || i == 0;
    }

    public boolean isAfterOrEqual(X x) {
        Value<X> other = with(x);
        return this.isAfterOrEqual(other);
    }

    public boolean isBefore(Value<X> other) {
        return this.compareTo(other) < 0;
    }

    public boolean isBeforeOrEqual(Value<X> other) {
        int i = this.compareTo(other);
        return i < 0 || i == 0;
    }

    public boolean isBeforeOrEqual(X x) {
        Value<X> other = this.with(x);
        return this.isAfterOrEqual(other);
    }
}
