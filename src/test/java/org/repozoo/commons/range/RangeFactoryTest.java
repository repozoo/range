package org.repozoo.commons.range;

import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;

class RangeFactoryTest {

    @Test
    void test_subtractWithYearMonth() {
        UnaryOperator<YearMonth> next = m -> m.plusMonths(1);
        UnaryOperator<YearMonth> previous = m -> m.plusMonths(-1);
        RangeFactory.CreateRange<YearMonth> createRange = RangeFactory.forType(YearMonth.class)
                .withComparator(Comparator.naturalOrder())
                .withIterator(next, previous)
                .build();

        YearMonth two = YearMonth.parse("2021-02");
        YearMonth four = YearMonth.parse("2021-04");
        YearMonth five = YearMonth.parse("2021-05");
        YearMonth six = YearMonth.parse("2021-06");
        YearMonth seven = YearMonth.parse("2021-07");
        YearMonth eight = YearMonth.parse("2021-08");
        YearMonth nine = YearMonth.parse("2021-09");
        YearMonth eleven = YearMonth.parse("2021-11");

        RangeI<YearMonth> fourToNine = createRange.between(four, nine);
        RangeI<YearMonth> twoToSix = createRange.between(two, six);
        RangeI<YearMonth> sixToEleven = createRange.between(six, eleven);
        RangeI<YearMonth> sixToSeven = createRange.between(six, seven);
        RangeI<YearMonth> twoToFour = createRange.between(two, four);

        assertThat(fourToNine.remove(RangeSet.empty()).getRanges()).containsExactly(createRange.between(four, nine));

        assertThat(fourToNine.remove(fourToNine).getRanges()).isEmpty();

        assertThat(fourToNine.remove(twoToSix).getRanges()).containsExactly(createRange.between(seven, nine));

        assertThat(fourToNine.remove(sixToEleven).getRanges()).containsExactly(createRange.between(four, five));

        assertThat(fourToNine.remove(sixToSeven).getRanges()).containsExactly(
                createRange.between(four, five),
                createRange.between(eight, nine));

        assertThat(fourToNine.remove(twoToFour).getRanges()).containsExactly(createRange.between(five, nine));
    }

    @Test
    void test_subtractWithInteger() {
        UnaryOperator<Integer> next = n -> n + 1;
        UnaryOperator<Integer> previous = n -> n - 1;
        RangeFactory.CreateRange<Integer> createRange = RangeFactory.forType(Integer.class)
                .withComparator(Comparator.naturalOrder())
                .withIterator(next, previous)
                .build();

        Integer two = 2;
        Integer four = 4;
        Integer five = 5;
        Integer six = 6;
        Integer seven = 7;
        Integer eight = 8;
        Integer nine = 9;
        Integer eleven = 11;

        RangeI<Integer> fourToNine = createRange.between(four, nine);
        RangeI<Integer> twoToSix = createRange.between(two, six);
        RangeI<Integer> sixToEleven = createRange.between(six, eleven);
        RangeI<Integer> sixToSeven = createRange.between(six, seven);
        RangeI<Integer> twoToFour = createRange.between(two, four);

        assertThat(fourToNine.remove(fourToNine).getRanges()).isEmpty();

        assertThat(fourToNine.remove(RangeSet.empty()).getRanges()).containsExactly(createRange.between(four, nine));

        assertThat(fourToNine.remove(twoToSix).getRanges()).containsExactly(createRange.between(seven, nine));

        assertThat(fourToNine.remove(sixToEleven).getRanges()).containsExactly(createRange.between(four, five));

        assertThat(fourToNine.remove(sixToSeven).getRanges()).containsExactly(
                createRange.between(four, five),
                createRange.between(eight, nine)
        );

        assertThat(fourToNine.remove(twoToFour).getRanges()).containsExactly(createRange.between(five, nine));
    }
}