package com.calendar.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.calendar.addMonths
import com.calendar.getDaysForMonth
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun CustomCalendar(
    modifier: Modifier = Modifier,
    currentDate: Date = Date(),
    onDateSelected: (Date) -> Unit = {},
    dayTextStyle: TextStyle = TextStyle.Default,
    headerTextStyle: TextStyle = TextStyle.Default,
    containerColor: Color = Color.White,
    headerColor: Color = Color.White,
    onMonthChange: (Date) -> Unit = {},
    colorSelectedDate: Color = Color.Transparent
) {
    var displayedDate by remember { mutableStateOf(currentDate) }

    val daysInMonth = remember(displayedDate) {
        getDaysForMonth(displayedDate)
    }
    val selectedDate = remember {
        mutableStateOf(currentDate)
    }
    Column(
        modifier = modifier
            .background(containerColor)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(headerColor)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                displayedDate = displayedDate.addMonths(-1)
                onMonthChange(displayedDate)
            }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous month")
            }

            Text(
                text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(displayedDate),
                style = headerTextStyle
            )

            IconButton(onClick = {
                displayedDate = displayedDate.addMonths(1)
                onMonthChange(displayedDate)
            }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next month")
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach {
                Text(text = it, style = dayTextStyle, textAlign = TextAlign.Center)
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(), userScrollEnabled = false
        ) {
            items(daysInMonth.size) { index ->
                val day = daysInMonth[index]
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .aspectRatio(1f)
                        .clickable {
                            if (day.calendar != null) {
                                onDateSelected(day.calendar.time)
                                selectedDate.value = day.calendar.time
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (day.isEmpty) {
                        Text(
                            text = day.dayNumber.toString(),
                            color = if(day.calendar?.time == selectedDate.value) colorSelectedDate else Color.Gray,
                            style = dayTextStyle,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        day.calendar?.let { calendar ->
                            Text(
                                text = calendar.get(Calendar.DAY_OF_MONTH).toString(),
                                color =  if(day.calendar?.time == selectedDate.value) colorSelectedDate else Color.Black,
                                style = dayTextStyle,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}