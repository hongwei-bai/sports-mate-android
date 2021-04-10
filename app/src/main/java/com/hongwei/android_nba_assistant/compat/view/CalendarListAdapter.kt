package com.hongwei.android_nba_assistant.compat.view

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hongwei.android_nba_assistant.R
import com.hongwei.android_nba_assistant.constant.AppConfigurations.Debug.DEBUG_CALENDAR
import com.hongwei.android_nba_assistant.databinding.LayoutCalendarDayBinding
import com.hongwei.android_nba_assistant.datasource.local.LocalSettings
import com.hongwei.android_nba_assistant.usecase.MatchEvent
import com.hongwei.android_nba_assistant.util.DrawableByNameUtil
import com.hongwei.android_nba_assistant.util.LocalDateTimeUtil.CALENDAR_GAME_DATE_FORMAT
import com.hongwei.android_nba_assistant.util.LocalDateTimeUtil.DAYS_PER_WEEK
import com.hongwei.android_nba_assistant.util.LocalDateTimeUtil.dayIdentifierToCalendar
import com.hongwei.android_nba_assistant.util.LocalDateTimeUtil.debugDateTime
import com.hongwei.android_nba_assistant.util.LocalDateTimeUtil.getBeginOfDay
import com.hongwei.android_nba_assistant.util.LocalDateTimeUtil.getDayIdentifier
import com.hongwei.android_nba_assistant.util.LocalDateTimeUtil.getDayIdentifierShift
import com.hongwei.android_nba_assistant.util.LocalDateTimeUtil.getLocalDateDisplay
import com.hongwei.android_nba_assistant.util.LocalDateTimeUtil.getLocalTimeDisplay
import com.hongwei.android_nba_assistant.util.LocalDateTimeUtil.getMondayOfWeek
import com.hongwei.android_nba_assistant.util.LocalDateTimeUtil.getSundayOfWeek
import java.util.*

class CalendarListAdapter(
    private val applicationContext: Context,
    private val localSettings: LocalSettings
) : RecyclerView.Adapter<CalendarListAdapter.MyViewHolder>() {
    var data: List<MatchEvent> = emptyList()
        set(value) {
            field = value
            eventByPositionHashMap.clear()
            value.forEach { matchEvent ->
                val position = dayIdList.indexOf(getDayIdentifier(matchEvent.date))
                eventByPositionHashMap[position] = matchEvent
            }
            debugSetData()
        }

    private val dayIdList: MutableList<Long> = MutableList(localSettings.scheduleWeeks * DAYS_PER_WEEK) { 0L }

    private var eventByPositionHashMap: HashMap<Int, MatchEvent> = hashMapOf()

    init {
        val firstDay = if (localSettings.startsFromMonday) getMondayOfWeek() else getSundayOfWeek()
        val firstDayId = getDayIdentifier(firstDay)
        for (weekNo in 0 until localSettings.scheduleWeeks) {
            for (weekday in 0 until DAYS_PER_WEEK) {
                val index = weekNo * DAYS_PER_WEEK + weekday
                dayIdList[index] = getDayIdentifierShift(firstDayId, index)
            }
        }
        debugInitBlock()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = LayoutCalendarDayBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(applicationContext, dayIdList[position], eventByPositionHashMap[position])
    }

    override fun getItemCount(): Int = localSettings.scheduleWeeks * DAYS_PER_WEEK

    class MyViewHolder(private val binding: LayoutCalendarDayBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, dayId: Long, event: MatchEvent?) {
            paintPastDays(context, binding, dayId)
            binding.dateHeader.text = getLocalDateDisplay(dayIdentifierToCalendar(dayId), CALENDAR_GAME_DATE_FORMAT)
            event?.let {
                binding.opponentLogoFrame.background = if (it.isHome) {
                    ContextCompat.getDrawable(context, R.drawable.bg_calendar_day_home_game)
                } else {
                    ContextCompat.getDrawable(context, R.color.warriors_transparent)
                }
                binding.gameLocation.text = event.location.toUpperCase(Locale.US)
                binding.opponentLogo.setImageDrawable(DrawableByNameUtil.getTeamDrawable(context, event.teamShort))
                binding.gameTime.text = getLocalTimeDisplay(event.date)
            } ?: clearItem(context, binding)
        }

        private fun paintPastDays(context: Context, binding: LayoutCalendarDayBinding, dayId: Long) {
            when (dayId) {
                getBeginOfDay().timeInMillis -> {
                    binding.root.background = context.getDrawable(R.color.calendar_red_alpha_background)
                    binding.dateHeader.setTextColor(Color.RED)
                }
                in Long.MIN_VALUE until getBeginOfDay().timeInMillis -> {
                    binding.root.background = context.getDrawable(R.color.calendar_black_alpha_past_background)
                    binding.dateHeader.setTextColor(Color.GRAY)
                }
                in getBeginOfDay().timeInMillis..Long.MAX_VALUE -> {
                    binding.root.background = context.getDrawable(R.color.calendar_black_alpha_background)
                    binding.dateHeader.setTextColor(Color.WHITE)
                }
            }
        }

        private fun clearItem(context: Context, binding: LayoutCalendarDayBinding) {
            with(binding) {
                opponentLogoFrame.background = ContextCompat.getDrawable(context, R.color.warriors_transparent)
                gameLocation.text = ""
                opponentLogo.setImageDrawable(null)
                gameTime.text = ""
            }
        }
    }

    private fun debugSetData() {
        if (DEBUG_CALENDAR) {
            data.forEachIndexed { index, matchEvent ->
                Log.d("bbbb", "setData[$index] ${debugDateTime(matchEvent.date)} ${matchEvent.opponentAbbrev}")
            }
            eventByPositionHashMap.forEach { t, u ->
                Log.d("bbbb", "setData trigger eventByPositionHashMap[$t] -> $u")
            }
        }
    }

    private fun debugInitBlock() {
        if (DEBUG_CALENDAR) {
            dayIdList.forEachIndexed { i, l ->
                Log.d(
                    "bbbb", "init dayIdList[$i] dayId: $l, " +
                            "display: ${debugDateTime(Calendar.getInstance().apply { timeInMillis = l })}"
                )
            }
        }
    }
}