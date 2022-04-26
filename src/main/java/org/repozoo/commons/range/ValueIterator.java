package org.repozoo.commons.range;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.function.UnaryOperator;

@AllArgsConstructor
class ValueIterator<Y> {

    private final UnaryOperator<Y> next;
    private final UnaryOperator<Y> previous;

    public Y next(Y value) {
        return next.apply(value);
    }

    public Y previous(Y value) {
        return previous.apply(value);
    }
}
