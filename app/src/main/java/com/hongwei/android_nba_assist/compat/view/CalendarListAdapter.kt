package com.hongwei.android_nba_assist.compat.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hongwei.android_nba_assist.R
import com.hongwei.android_nba_assist.constant.AppConfigurations.TeamScheduleConfiguration.MARK_TODAY_EVENT_DIM_AFTER_HOURS
import com.hongwei.android_nba_assist.databinding.LayoutCalendarDayBinding
import com.hongwei.android_nba_assist.datasource.local.LocalSettings
import com.hongwei.android_nba_assist.util.DebugUtil.debugInitBlock
import com.hongwei.android_nba_assist.util.DebugUtil.debugSetData
import com.hongwei.android_nba_assist.util.LocalDateTimeUtil.CALENDAR_GAME_DATE_FORMAT
import com.hongwei.android_nba_assist.util.LocalDateTimeUtil.DAYS_PER_WEEK
import com.hongwei.android_nba_assist.util.LocalDateTimeUtil.MILLIS_PER_HOUR
import com.hongwei.android_nba_assist.util.LocalDateTimeUtil.dayIdentifierToCalendar
import com.hongwei.android_nba_assist.util.LocalDateTimeUtil.getBeginOfDay
import com.hongwei.android_nba_assist.util.LocalDateTimeUtil.getDayIdentifier
import com.hongwei.android_nba_assist.util.LocalDateTimeUtil.getDayIdentifierShift
import com.hongwei.android_nba_assist.util.LocalDateTimeUtil.getLastMondayForSunday
import com.hongwei.android_nba_assist.util.LocalDateTimeUtil.getLocalDateDisplay
import com.hongwei.android_nba_assist.util.LocalDateTimeUtil.getLocalTimeDisplay
import com.hongwei.android_nba_assist.util.LocalDateTimeUtil.getSundayOfWeek
import com.hongwei.android_nba_assist.compat.viewmodel.viewobject.MatchEvent
import com.squareup.picasso.Picasso
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
                notifyItemChanged(position)
            }
            debugSetData(value, eventByPositionHashMap)
        }

    private val dayIdList: MutableList<Long> = MutableList(localSettings.scheduleWeeks * DAYS_PER_WEEK) { 0L }

    private var eventByPositionHashMap: HashMap<Int, MatchEvent> = hashMapOf()

    init {
        val firstDay = if (localSettings.startsFromMonday) getLastMondayForSunday() else getSundayOfWeek()
        val firstDayId = getDayIdentifier(firstDay)
        for (weekNo in 0 until localSettings.scheduleWeeks) {
            for (weekday in 0 until DAYS_PER_WEEK) {
                val index = weekNo * DAYS_PER_WEEK + weekday
                dayIdList[index] = getDayIdentifierShift(firstDayId, index)
            }
        }
        debugInitBlock(dayIdList)
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
            paintPastDays(context, binding, dayId, event?.date)
            binding.dateHeader.text = getLocalDateDisplay(dayIdentifierToCalendar(dayId), CALENDAR_GAME_DATE_FORMAT)
            event?.let {
                binding.opponentLogoFrame.background = if (it.isHome) {
                    (ContextCompat.getDrawable(context, R.drawable.bg_calendar_day_home_game) as? GradientDrawable)?.apply {
                        setGradientCenter(0.85f, 0.85f)
                        val majorColor = context.getColor(R.color.warriors_gold)
                        colors = intArrayOf(
                            majorColor, majorColor, majorColor,
                            majorColor, majorColor, majorColor,
                            context.getColor(R.color.grey80)
                        )
                    }
                } else {
                    ContextCompat.getDrawable(context, android.R.color.transparent)
                }
                binding.gameLocation.text = event.location.toUpperCase(Locale.ROOT)
                Picasso.get().load(event.opponentLogoUrl).placeholder(event.opponentLogoPlaceholder).into(binding.opponentLogo)
                binding.gameTime.text = getLocalTimeDisplay(event.date)
                fun hideResult() {
                    binding.gameResult.visibility = View.GONE
                }
                event.result?.run {
                    binding.gameResult.visibility = View.VISIBLE
                    binding.gameResult.text = "$winLossSymbol$currentTeamScore-$opponentTeamScore"
                } ?: hideResult()
            } ?: clearItem(context, binding)
        }

        private fun paintPastDays(context: Context, binding: LayoutCalendarDayBinding, dayId: Long, eventTime: Calendar?) {
            var colorStateList: ColorStateList? = null
            when (dayId) {
                getBeginOfDay().timeInMillis -> {
                    binding.root.background = context.getDrawable(R.drawable.bg_calendar_today)
                    binding.dateHeader.background = context.getDrawable(R.color.calendar_red_background)
                    colorStateList = eventTime?.let {
                        if (Calendar.getInstance().after(Calendar.getInstance().apply {
                                timeInMillis = it.timeInMillis + MARK_TODAY_EVENT_DIM_AFTER_HOURS * MILLIS_PER_HOUR
                            })) ColorStateList.valueOf(context.getColor(R.color.grey60))
                        else ColorStateList.valueOf(context.getColor(R.color.white))
                    } ?: ColorStateList.valueOf(context.getColor(R.color.white))
                    binding.opponentLogoFrame.alpha = 1f
                }
                in Long.MIN_VALUE until getBeginOfDay().timeInMillis -> {
                    binding.root.background = context.getDrawable(R.color.calendar_black_alpha_past_background)
                    binding.dateHeader.background = context.getDrawable(R.color.grey80)
                    colorStateList = ColorStateList.valueOf(context.getColor(R.color.grey60))
                    binding.opponentLogoFrame.alpha = 0.2f
                }
                in getBeginOfDay().timeInMillis..Long.MAX_VALUE -> {
                    binding.root.background = context.getDrawable(R.color.calendar_black_alpha_background)
                    binding.dateHeader.background = context.getDrawable(R.color.grey80)
                    colorStateList = ColorStateList.valueOf(context.getColor(R.color.white))
                    binding.opponentLogoFrame.alpha = 1f
                }
            }
            binding.dateHeader.setTextColor(colorStateList)
            binding.gameLocation.setTextColor(colorStateList)
            binding.gameTime.setTextColor(colorStateList)
        }

        private fun clearItem(context: Context, binding: LayoutCalendarDayBinding) {
            with(binding) {
                opponentLogoFrame.background = ContextCompat.getDrawable(context, android.R.color.transparent)
                gameLocation.text = ""
                opponentLogo.setImageDrawable(null)
                gameTime.text = ""
            }
        }
    }
}