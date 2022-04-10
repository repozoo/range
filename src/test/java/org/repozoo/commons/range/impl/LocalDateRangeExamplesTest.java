package org.repozoo.commons.range.impl;

import org.repozoo.commons.range.factory.impl.LocalDateRange;
import org.repozoo.commons.range.Range;
import org.repozoo.commons.range.RangeSet;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;

class LocalDateRangeExamplesTest {

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
        assertThat(everybodyIsPresent.stream().flatMap(RangeSet::stream))
                .containsExactly(
                        LocalDateRange.between(dec2021.atDay(10), dec2021.atDay(13))
                );
    }
}