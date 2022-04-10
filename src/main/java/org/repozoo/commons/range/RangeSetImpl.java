package org.repozoo.commons.range;

import lombok.Value;

import java.util.List;
import java.util.stream.Stream;

@Value
class RangeSetImpl<T> implements RangeSet<T> {

    List<Range<T>> ranges;

    @Override
    public Stream<Range<T>> stream() {
        return ranges.stream();
    }

    @Override
    public List<Range<T>> getRanges() {
        return ranges;
    }
}
