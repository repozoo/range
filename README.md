# range
- - - -
Range is a small library,<br>
trying to cover the basic concepts of a range (or interval) - a contiguous span of values **from _start_ to _end_** _inclusive_ -<br>
including a set of functions to interact with 'em.<br><br> 
You can either
- use predefined Range factories
  - `Range<YearMonth> range = YearMonthRange.of(start, end);`
  - `Range<LocalDate> range = LocalDateRange.of(start, end);`
  - `Range<LocalDateTime> range = LocalDateTimeRange.of(start, end);`
  - ...
- or create custom ones
- - - -
## API
<img src="./documentation/images/range-API.svg" alt="range-api">

## Usage
### Examples
#### add - Example
```java
// Assing
YearMonth dec2021 = YearMonth.parse("2021-12");
RangeSet<LocalDate> vacationSahrah = RangeSet.of(
    LocalDateRange.of(dec2021.atDay(1), dec2021.atDay(5)),
    LocalDateRange.of(dec2021.atDay(14), dec2021.atDay(26)),
    LocalDateRange.of(dec2021.atDay(28), dec2021.atDay(31))
);
RangeSet<LocalDate> vacationJimmy = RangeSet.of(
    LocalDateRange.of(dec2021.atDay(1), dec2021.atDay(9)),
    LocalDateRange.of(dec2021.atDay(23), dec2021.atDay(29))
);
RangeSet<LocalDate> vacationReggy = RangeSet.of(
    LocalDateRange.of(dec2021.atDay(1), dec2021.atDay(9))
);
// Act
RangeSet<LocalDate> atLeastOneIsAbsent = vacationSahrah.add(vacationJimmy).add(vacationReggy);
// Assert
assertThat(atLeastOneIsAbsent.stream()).containsExactly(
    LocalDateRange.of(dec2021.atDay(1), dec2021.atDay(9)),
    LocalDateRange.of(dec2021.atDay(14), dec2021.atDay(31))
);
```
#### intersection - Example
```java
// Assing
YearMonth dec2021 = YearMonth.parse("2021-12");
RangeSet<LocalDate> vacationSahrah = RangeSet.of(
    LocalDateRange.of(dec2021.atDay(1), dec2021.atDay(5)),
    LocalDateRange.of(dec2021.atDay(14), dec2021.atDay(26)),
    LocalDateRange.of(dec2021.atDay(28), dec2021.atDay(31))
);
RangeSet<LocalDate> vacationJimmy = RangeSet.of(
    LocalDateRange.of(dec2021.atDay(1), dec2021.atDay(9)),
    LocalDateRange.of(dec2021.atDay(23), dec2021.atDay(29))
);
// Act
RangeSet<LocalDate> everybodyIsAbsent = vacationSahrah.intersection(vacationJimmy);
// Assert
assertThat(everybodyIsAbsent.stream()).containsExactly(
    LocalDateRange.of(dec2021.atDay(1), dec2021.atDay(5)),
    LocalDateRange.of(dec2021.atDay(23), dec2021.atDay(26)),
    LocalDateRange.of(dec2021.atDay(28), dec2021.atDay(29))
);
```

## Architecture
TODO ...

<img src="./documentation/images/range-and-rangeset-uml.svg" alt="range-uml">