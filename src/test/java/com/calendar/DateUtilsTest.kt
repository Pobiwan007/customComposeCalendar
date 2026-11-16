package com.calendar

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

class DateUtilsTest {

    private fun date(year: Int, month: Int, day: Int, hour: Int = 0) =
        GregorianCalendar(year, month, day, hour, 0).time

    @Test
    fun `february 2026 with monday start is padded with the end of january`() {
        // 1 Feb 2026 is a Sunday -> six days of January before it.
        val days = getDaysForMonth(date(2026, Calendar.FEBRUARY, 10), Calendar.MONDAY)

        val leading = days.takeWhile { it.isEmpty }
        assertEquals(listOf(26, 27, 28, 29, 30, 31), leading.map { it.dayNumber })
        assertEquals((1..28).toList(), days.filterNot { it.isEmpty }.map { it.dayNumber })
        assertEquals(listOf(1), days.takeLastWhile { it.isEmpty }.map { it.dayNumber })
        assertEquals(35, days.size)
    }

    @Test
    fun `february 2026 with sunday start needs no padding`() {
        val days = getDaysForMonth(date(2026, Calendar.FEBRUARY, 1), Calendar.SUNDAY)

        assertEquals(28, days.size)
        assertTrue(days.none { it.isEmpty })
    }

    @Test
    fun `leap february 2024 has 29 days and correct padding`() {
        // 1 Feb 2024 is a Thursday.
        val days = getDaysForMonth(date(2024, Calendar.FEBRUARY, 15), Calendar.MONDAY)

        assertEquals(listOf(29, 30, 31), days.takeWhile { it.isEmpty }.map { it.dayNumber })
        assertEquals(29, days.count { !it.isEmpty })
        assertEquals(listOf(1, 2, 3), days.takeLastWhile { it.isEmpty }.map { it.dayNumber })
    }

    @Test
    fun `grid size is always a multiple of seven`() {
        for (startOfWeek in Calendar.SUNDAY..Calendar.SATURDAY) {
            for (month in Calendar.JANUARY..Calendar.DECEMBER) {
                val days = getDaysForMonth(date(2025, month, 1), startOfWeek)
                assertEquals("month=$month start=$startOfWeek", 0, days.size % 7)
            }
        }
    }

    @Test
    fun `month days carry their calendar date`() {
        val days = getDaysForMonth(date(2026, Calendar.MARCH, 5))

        val tenth = days.first { !it.isEmpty && it.dayNumber == 10 }
        assertEquals(2026, tenth.calendar!!.get(Calendar.YEAR))
        assertEquals(Calendar.MARCH, tenth.calendar!!.get(Calendar.MONTH))
        assertEquals(10, tenth.calendar!!.get(Calendar.DAY_OF_MONTH))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invalid startOfWeek is rejected`() {
        getDaysForMonth(date(2026, Calendar.MARCH, 5), startOfWeek = 0)
    }

    @Test
    fun `addMonths clamps to the last day of a shorter month`() {
        val result = date(2026, Calendar.JANUARY, 31).addMonths(1)
        assertTrue(result.isSameDay(date(2026, Calendar.FEBRUARY, 28)))
    }

    @Test
    fun `isSameDay ignores time of day`() {
        assertTrue(date(2026, Calendar.MAY, 1, hour = 1).isSameDay(date(2026, Calendar.MAY, 1, hour = 23)))
        assertFalse(date(2026, Calendar.MAY, 1).isSameDay(date(2026, Calendar.MAY, 2)))
    }

    @Test
    fun `weekday labels start from the requested day`() {
        assertEquals(
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
            weekdayLabels(Calendar.MONDAY, Locale.US)
        )
        assertEquals("Sun", weekdayLabels(Calendar.SUNDAY, Locale.US).first())
    }
}
