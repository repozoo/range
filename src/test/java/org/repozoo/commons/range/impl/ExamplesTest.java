package org.repozoo.commons.range.impl;

import org.repozoo.commons.range.Range;
import org.repozoo.commons.range.RangeFactory;
import org.repozoo.commons.range.factories.LocalDateRange;
import org.repozoo.commons.range.RangeSet;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unused")
class ExamplesTest {

    @Test
    void createYearMonthRange_example() {
        //build up the factory
        UnaryOperator<YearMonth> next = n -> n.plusMonths(1);
        UnaryOperator<YearMonth> previous = n ->  n.minusMonths(1);
        RangeFactory.CreateRange<YearMonth> createRange = RangeFactory.forType(YearMonth.class)
                .withComparator(YearMonth::compareTo)
                .withIterator(next, previous)
                .build();

        // use the factory
        YearMonth jan = YearMonth.parse("2022-01");
        YearMonth dec = YearMonth.parse("2022-12");
        Range<YearMonth> range = createRange.between(jan, dec);
    }

    @Test
    void add_example() {
        // Assing
        YearMonth dec2021 = YearMonth.parse("2021-12");
        RangeSet<LocalDate> vacationSahrah = RangeSet.of(
                LocalDateRange.between(dec2021.atDay(1), dec2021.atDay(5)),
                LocalDateRange.between(dec2021.atDay(14), dec2021.atDay(26)),
                LocalDateRange.between(dec2021.atDay(28), dec2021.atDay(31))
        );
        RangeSet<LocalDate> vacationJimmy = RangeSet.of(
                LocalDateRange.between(dec2021.atDay(1), dec2021.atDay(9)),
                LocalDateRange.between(dec2021.atDay(23), dec2021.atDay(29))
        );
        RangeSet<LocalDate> vacationReggy = RangeSet.of(
                LocalDateRange.between(dec2021.atDay(1), dec2021.atDay(9))
        );
        // Act
        RangeSet<LocalDate> atLeastOneIsAbsent = vacationSahrah.add(vacationJimmy).add(vacationReggy);
        // Assert
        assertThat(atLeastOneIsAbsent.stream())
                .containsExactly(
                        LocalDateRange.between(dec2021.atDay(1), dec2021.atDay(9)),
                        LocalDateRange.between(dec2021.atDay(14), dec2021.atDay(31))
                );
    }

    @Test
    void remove_example() {
        // Assing
        YearMonth dec2021 = YearMonth.parse("2021-12");
        Range<LocalDate> december = LocalDateRange.between(dec2021.atDay(1), dec2021.atEndOfMonth());
        RangeSet<LocalDate> vacationSahrah = RangeSet.of(
                LocalDateRange.between(dec2021.atDay(1), dec2021.atDay(5)),
                LocalDateRange.between(dec2021.atDay(14), dec2021.atDay(26)),
                LocalDateRange.between(dec2021.atDay(28), dec2021.atDay(31))
        );
        RangeSet<LocalDate> vacationJimmy = RangeSet.of(
                LocalDateRange.between(dec2021.atDay(1), dec2021.atDay(9)),
                LocalDateRange.between(dec2021.atDay(23), dec2021.atDay(29))
        );
        // Act
        RangeSet<LocalDate> everybodyIsPresent = december.remove(vacationSahrah).remove(vacationJimmy);
        // Assert
        assertThat(everybodyIsPresent.stream())
                .containsExactly(
                        LocalDateRange.between(dec2021.atDay(10), dec2021.atDay(13))
                );
    }

    @Test
    void intersection_example() {
        // Assing
        YearMonth dec2021 = YearMonth.parse("2021-12");
        RangeSet<LocalDate> vacationSahrah = RangeSet.of(
                LocalDateRange.between(dec2021.atDay(1), dec2021.atDay(5)),
                LocalDateRange.between(dec2021.atDay(14), dec2021.atDay(26)),
                LocalDateRange.between(dec2021.atDay(28), dec2021.atDay(31))
        );
        RangeSet<LocalDate> vacationJimmy = RangeSet.of(
                LocalDateRange.between(dec2021.atDay(1), dec2021.atDay(9)),
                LocalDateRange.between(dec2021.atDay(23), dec2021.atDay(29))
        );
        // Act
        RangeSet<LocalDate> everybodyIsAbsent = vacationSahrah.intersection(vacationJimmy);
        // Assert
        assertThat(everybodyIsAbsent.stream())
                .containsExactly(
                        LocalDateRange.between(dec2021.atDay(1), dec2021.atDay(5)),
                        LocalDateRange.between(dec2021.atDay(23), dec2021.atDay(26)),
                        LocalDateRange.between(dec2021.atDay(28), dec2021.atDay(29))
                );
    }
}