# customComposeCalendar

[![CI](https://github.com/Pobiwan007/customComposeCalendar/actions/workflows/ci.yml/badge.svg)](https://github.com/Pobiwan007/customComposeCalendar/actions/workflows/ci.yml)
[![JitPack](https://jitpack.io/v/Pobiwan007/customComposeCalendar.svg)](https://jitpack.io/#Pobiwan007/customComposeCalendar)
![minSdk](https://img.shields.io/badge/minSdk-24-brightgreen)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)

A lightweight, fully customizable month calendar for Jetpack Compose with no dependencies
beyond Compose itself.

- configurable first day of the week (Monday, Sunday, …) and locale-aware labels
- month navigation with callbacks
- every colour and text style is a parameter
- safe to put inside a `verticalScroll` / `LazyColumn` (the grid is not lazy)
- grid logic is a pure function covered by unit tests

## Usage

```kotlin
CustomCalendar(
    currentDate = Date(),
    startOfWeek = Calendar.MONDAY,
    onDateSelected = { date -> viewModel.onDateSelected(date) },
    onMonthChange = { month -> viewModel.loadEvents(month) },
    headerTextStyle = MaterialTheme.typography.titleMedium,
    dayTextStyle = MaterialTheme.typography.bodyMedium,
    selectedDayBackground = MaterialTheme.colorScheme.primaryContainer,
)
```

| Parameter | Default | Description |
|---|---|---|
| `currentDate` | `Date()` | Initially displayed month and selected day |
| `startOfWeek` | `Calendar.MONDAY` | First column of the grid |
| `locale` | `Locale.getDefault()` | Month and weekday names |
| `onDateSelected` / `onMonthChange` | no-op | Selection and navigation callbacks |
| `dayColor`, `outOfMonthDayColor`, `colorSelectedDate`, `selectedDayBackground` | — | Day colours |
| `containerColor`, `headerColor`, `dayTextStyle`, `headerTextStyle` | — | Container and typography |

The grid itself is available as a plain function, handy for custom UIs or tests:

```kotlin
val days: List<DayItem> = getDaysForMonth(date, startOfWeek = Calendar.SUNDAY)
// always a multiple of 7; padding days from adjacent months have isEmpty = true
```

## Installation

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories { maven("https://jitpack.io") }
}

// build.gradle.kts
dependencies {
    implementation("com.github.Pobiwan007:customComposeCalendar:v1.0.0")
}
```

## Build & test

```bash
./gradlew testReleaseUnitTest assembleRelease
```

## Design notes

- **Why `java.util.Calendar` and not `java.time`?** `minSdk` is 24, and `java.time` would force every
  consumer to enable core library desugaring.
- **Why not `LazyVerticalGrid`?** A month has at most 42 cells, so laziness buys nothing, while a lazy grid
  inside a vertically scrollable parent crashes with infinite height constraints.
