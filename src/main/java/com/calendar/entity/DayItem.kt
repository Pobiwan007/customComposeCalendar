package com.calendar.entity

import java.util.Calendar

data class DayItem(val calendar: Calendar?, val isEmpty: Boolean, val dayNumber: Int? = null)