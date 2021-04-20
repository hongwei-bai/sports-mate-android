package com.hongwei.android_nba_assistant.util

import org.jetbrains.annotations.TestOnly
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*
import kotlin.math.abs
import kotlin.math.roundToInt

object LocalDateTimeUtil {
    const val MILLIS_PER_SECOND = 1000L
    const val MILLIS_PER_MINUTE = 1000 * 60L
    const val MILLIS_PER_HOUR = 1000 * 3600L
    private const val MILLIS_PER_DAY = 1000 * 3600 * 24L
    const val DAYS_PER_WEEK = 7
    const val MILLIS_PER_WEEK = MILLIS_PER_DAY * 7

    const val CALENDAR_GAME_DATE_FORMAT = "EEE MMM d"
    private const val DASHBOARD_UPCOMING_GAME_DATE_FORMAT = "EEE, MMM d"
    private const val DASHBOARD_UPCOMING_GAME_TIME_FORMAT = "H:mm a"

    fun getLocalDateDisplay(calendar: Calendar, format: String = DASHBOARD_UPCOMING_GAME_DATE_FORMAT): String =
        SimpleDateFormat(format, Locale.US).format(calendar.time).toUpperCase(Locale.US)

    fun getLocalTimeDisplay(calendar: Calendar, format: String = DASHBOARD_UPCOMING_GAME_TIME_FORMAT): String =
        SimpleDateFormat(format, Locale.US).format(calendar.time)

    fun getDateInWeeks(week: Int): Calendar {
        val calendar: Calendar = getInstance()
        calendar.timeInMillis = calendar.timeInMillis + MILLIS_PER_WEEK * week
        return calendar
    }

    fun unixTimeStampToCalendar(unixTimeStamp: Long): Calendar = getInstance().apply {
        timeInMillis = unixTimeStamp
    }

    fun getDayIdentifier(unixTimeStamp: Long): Long =
        getDayIdentifier(unixTimeStampToCalendar(unixTimeStamp))

    fun getDayIdentifier(calendar: Calendar): Long =
        getBeginOfDay(calendar).timeInMillis

    fun getDayIdentifierShift(dayId: Long, days: Int): Long = dayId + days * MILLIS_PER_DAY

    fun dayIdentifierToCalendar(dayId: Long): Calendar = getInstance().apply {
        timeInMillis = dayId
    }

    fun isFuture(calendar: Calendar): Boolean = calendar.timeInMillis > getInstance().timeInMillis

    fun getInHours(calendar: Calendar): Int = getInHours(calendar, getInstance())

    fun getHoursDiff(calendar: Calendar): Int = getHoursDiff(calendar, getInstance())

    fun getInMillis(calendar: Calendar): Long = calendar.timeInMillis - getInstance().timeInMillis

    @TestOnly
    fun getInHours(calendar: Calendar, reference: Calendar): Int =
        ((calendar.timeInMillis - reference.timeInMillis) * 1.0 / MILLIS_PER_HOUR).roundToInt()

    @TestOnly
    fun getHoursDiff(calendar: Calendar, reference: Calendar): Int =
        (abs(calendar.timeInMillis - reference.timeInMillis) * 1.0 / MILLIS_PER_HOUR).roundToInt()

    fun getInDays(calendar: Calendar): Int = getInDays(calendar, getInstance())

    @TestOnly
    fun getInDays(calendar: Calendar, reference: Calendar): Int =
        ((getBeginOfDay(calendar).timeInMillis - getBeginOfDay(reference).timeInMillis) / MILLIS_PER_DAY).toInt()

    fun getBeginOfDay(calendar: Calendar = getInstance()): Calendar {
        val newCalendar: Calendar = calendar.clone() as Calendar
        newCalendar.set(HOUR_OF_DAY, 0)
        newCalendar.set(MINUTE, 0)
        newCalendar.set(SECOND, 0)
        newCalendar.set(MILLISECOND, 0)
        return newCalendar
    }

    fun getAheadOfHours(hours: Int): Calendar = getAheadOfHours(hours, getInstance())

    @TestOnly
    fun getAheadOfHours(hours: Int, calendar: Calendar): Calendar {
        val newCalendar: Calendar = getInstance()
        newCalendar.timeInMillis = calendar.timeInMillis - hours * MILLIS_PER_HOUR
        return newCalendar
    }

    fun getEndOfDay(calendar: Calendar): Calendar {
        val newCalendar: Calendar = calendar.clone() as Calendar
        newCalendar.set(HOUR_OF_DAY, 23)
        newCalendar.set(MINUTE, 59)
        newCalendar.set(SECOND, 59)
        newCalendar.set(MILLISECOND, 999)
        return newCalendar
    }

    fun getMondayOfWeek(calendar: Calendar = getInstance()): Calendar = getWeekday(calendar, MONDAY)

    fun getSundayOfWeek(calendar: Calendar = getInstance()): Calendar = getWeekday(calendar, SUNDAY)

    fun getWeekday(calendar: Calendar = getInstance(), weekday: Int): Calendar {
        val newCalendar: Calendar = calendar.clone() as Calendar
        newCalendar.set(DAY_OF_WEEK, weekday)
        newCalendar.set(HOUR_OF_DAY, 0)
        newCalendar.set(MINUTE, 0)
        newCalendar.set(SECOND, 0)
        newCalendar.set(MILLISECOND, 0)
        return newCalendar
    }

    fun debugDateTime(calendar: Calendar): String =
        SimpleDateFormat("yyyy-MMM-dd HH:mm").format(calendar.time)
}