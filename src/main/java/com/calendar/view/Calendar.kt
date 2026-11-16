package com.calendar.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.calendar.addMonths
import com.calendar.entity.DayItem
import com.calendar.getDaysForMonth
import com.calendar.isSameDay
import com.calendar.weekdayLabels
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * A lightweight month calendar.
 *
 * The grid is built from plain [Row]s instead of a lazy grid, so the calendar can be placed
 * inside a vertically scrollable container without the "infinite height" crash.
 *
 * @param currentDate the initially displayed month and selected day.
 * @param startOfWeek first column of the grid, one of [Calendar.SUNDAY]..[Calendar.SATURDAY].
 * @param selectedDayBackground background of the selected day; transparent by default.
 */
@Composable
fun CustomCalendar(
    modifier: Modifier = Modifier,
    currentDate: Date = Date(),
    onDateSelected: (Date) -> Unit = {},
    onMonthChange: (Date) -> Unit = {},
    startOfWeek: Int = Calendar.MONDAY,
    locale: Locale = Locale.getDefault(),
    dayTextStyle: TextStyle = TextStyle.Default,
    headerTextStyle: TextStyle = TextStyle.Default,
    containerColor: Color = Color.White,
    headerColor: Color = Color.White,
    dayColor: Color = Color.Black,
    outOfMonthDayColor: Color = Color.Gray,
    colorSelectedDate: Color = Color.Black,
    selectedDayBackground: Color = Color.Transparent
) {
    var displayedDate by remember { mutableStateOf(currentDate) }
    var selectedDate by remember { mutableStateOf(currentDate) }

    val days = remember(displayedDate, startOfWeek) { getDaysForMonth(displayedDate, startOfWeek) }
    val weekdays = remember(startOfWeek, locale) { weekdayLabels(startOfWeek, locale) }
    val title = remember(displayedDate, locale) {
        SimpleDateFormat("LLLL yyyy", locale).format(displayedDate)
            .replaceFirstChar { it.titlecase(locale) }
    }

    Column(
        modifier = modifier
            .background(containerColor)
            .padding(16.dp)
    ) {
        MonthHeader(
            title = title,
            textStyle = headerTextStyle,
            background = headerColor,
            onPrevious = {
                displayedDate = displayedDate.addMonths(-1)
                onMonthChange(displayedDate)
            },
            onNext = {
                displayedDate = displayedDate.addMonths(1)
                onMonthChange(displayedDate)
            }
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            weekdays.forEach { label ->
                Text(
                    text = label,
                    style = dayTextStyle,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        days.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { day ->
                    DayCell(
                        day = day,
                        isSelected = day.calendar?.time?.isSameDay(selectedDate) == true,
                        textStyle = dayTextStyle,
                        dayColor = dayColor,
                        outOfMonthDayColor = outOfMonthDayColor,
                        selectedColor = colorSelectedDate,
                        selectedBackground = selectedDayBackground,
                        onClick = { date ->
                            selectedDate = date
                            onDateSelected(date)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthHeader(
    title: String,
    textStyle: TextStyle,
    background: Color,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous month")
        }
        Text(text = title, style = textStyle)
        IconButton(onClick = onNext) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next month")
        }
    }
}

@Composable
private fun DayCell(
    day: DayItem,
    isSelected: Boolean,
    textStyle: TextStyle,
    dayColor: Color,
    outOfMonthDayColor: Color,
    selectedColor: Color,
    selectedBackground: Color,
    onClick: (Date) -> Unit,
    modifier: Modifier = Modifier
) {
    val date = day.calendar?.time
    Box(
        modifier = modifier
            .padding(4.dp)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(if (isSelected) selectedBackground else Color.Transparent)
            .then(if (date != null) Modifier.clickable { onClick(date) } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.dayNumber?.toString().orEmpty(),
            style = textStyle,
            color = when {
                day.isEmpty -> outOfMonthDayColor
                isSelected -> selectedColor
                else -> dayColor
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomCalendarPreview() {
    CustomCalendar(selectedDayBackground = Color(0xFFE0E0FF))
}
