package com.calendar

import com.calendar.entity.DayItem
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

fun Date.addMonths(months: Int): Date {
    val calendar = Calendar.getInstance().apply { time = this@addMonths }

    calendar.add(Calendar.MONTH, months)
    return calendar.time
}
fun Date.toStartOfDay(): Date {
    val calendar = Calendar.getInstance().apply {
        time = this@toStartOfDay
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.time
}

fun getDaysForMonth(date: Date, startOfWeek: Int = Calendar.MONDAY): List<DayItem> {
    val calendar = Calendar.getInstance().apply { time = date }

    val previousMonthCalendar = calendar.clone() as Calendar
    previousMonthCalendar.add(Calendar.MONTH, -1)

    val nextMonthCalendar = calendar.clone() as Calendar
    nextMonthCalendar.add(Calendar.MONTH, 1)

    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfMonth = calendar.clone() as Calendar

    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    val firstDayOfWeek = (firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - startOfWeek + 7) % 7

    val days = (1..daysInMonth).map { day ->
        DayItem(
            calendar = (firstDayOfMonth.clone() as Calendar).apply {
                set(
                    Calendar.DAY_OF_MONTH,
                    day
                )
            },
            isEmpty = false,
            dayNumber = day
        )
    }

    val totalDays = firstDayOfWeek + days.size
    val emptyEndDays = if (totalDays % 7 == 0) 0 else 7 - (totalDays % 7)

    val emptyStartDays = List(firstDayOfWeek) { day ->
        val previousMonthDay = previousMonthCalendar.apply {
            set(
                Calendar.WEEK_OF_MONTH,
                previousMonthCalendar.getActualMaximum(Calendar.WEEK_OF_MONTH)
            )
            set(
                Calendar.DAY_OF_WEEK,
                Calendar.MONDAY + day
            )
        }
        DayItem(
            calendar = null,
            isEmpty = true,
            dayNumber = previousMonthDay.get(Calendar.DAY_OF_MONTH)  // Записываем число из предыдущего месяца
        )
    }

    val emptyEndDaysList = List(emptyEndDays) {
        val nextMonthDay = nextMonthCalendar.apply {
            set(Calendar.WEEK_OF_MONTH, 0)
            set(Calendar.DAY_OF_MONTH, it + 1)
        }
        DayItem(
            calendar = null,
            isEmpty = true,
            dayNumber = nextMonthDay.get(Calendar.DAY_OF_MONTH)  // Записываем число из следующего месяца
        )
    }

    return emptyStartDays + days + emptyEndDaysList
}