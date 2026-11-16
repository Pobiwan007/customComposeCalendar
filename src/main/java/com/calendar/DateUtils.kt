package com.calendar

import com.calendar.entity.DayItem
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Date.addMonths(months: Int): Date =
    Calendar.getInstance().apply {
        time = this@addMonths
        add(Calendar.MONTH, months)
    }.time

fun Date.toStartOfDay(): Date =
    Calendar.getInstance().apply {
        time = this@toStartOfDay
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time

fun Date.isSameDay(other: Date): Boolean = toStartOfDay() == other.toStartOfDay()

/**
 * Builds a month grid for the month containing [date].
 *
 * The result always has a multiple of 7 items: the days of the month are padded with the
 * trailing days of the previous month and the leading days of the next month
 * (`isEmpty = true`, `calendar = null`) so that the first column is [startOfWeek].
 *
 * @param startOfWeek one of [Calendar.SUNDAY]..[Calendar.SATURDAY].
 */
fun getDaysForMonth(date: Date, startOfWeek: Int = Calendar.MONDAY): List<DayItem> {
    require(startOfWeek in Calendar.SUNDAY..Calendar.SATURDAY) { "Invalid startOfWeek: $startOfWeek" }

    val firstOfMonth = Calendar.getInstance().apply {
        time = date.toStartOfDay()
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val daysInMonth = firstOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val leadingCount = (firstOfMonth.get(Calendar.DAY_OF_WEEK) - startOfWeek + 7) % 7

    val daysInPreviousMonth = (firstOfMonth.clone() as Calendar)
        .apply { add(Calendar.MONTH, -1) }
        .getActualMaximum(Calendar.DAY_OF_MONTH)

    val leadingDays = List(leadingCount) { index ->
        DayItem(calendar = null, isEmpty = true, dayNumber = daysInPreviousMonth - leadingCount + 1 + index)
    }

    val monthDays = List(daysInMonth) { index ->
        DayItem(
            calendar = (firstOfMonth.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, index + 1) },
            isEmpty = false,
            dayNumber = index + 1
        )
    }

    val trailingCount = (7 - (leadingCount + daysInMonth) % 7) % 7
    val trailingDays = List(trailingCount) { index ->
        DayItem(calendar = null, isEmpty = true, dayNumber = index + 1)
    }

    return leadingDays + monthDays + trailingDays
}

/** Short weekday names ordered so that the list starts with [startOfWeek]. */
fun weekdayLabels(startOfWeek: Int = Calendar.MONDAY, locale: Locale = Locale.getDefault()): List<String> {
    val names = DateFormatSymbols.getInstance(locale).shortWeekdays // 1-based: [1] = Sunday
    return List(7) { index -> names[(startOfWeek - 1 + index) % 7 + 1] }
}
