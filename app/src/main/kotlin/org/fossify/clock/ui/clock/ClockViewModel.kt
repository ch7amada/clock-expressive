package org.fossify.clock.ui.clock

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.fossify.clock.R
import org.fossify.clock.extensions.config
import org.fossify.clock.helpers.CLOCK_FACE_ANALOGUE
import org.fossify.clock.helpers.CLOCK_FACE_DIGITAL
import org.fossify.clock.extensions.getAllTimeZonesModified
import org.fossify.clock.extensions.getClosestEnabledAlarmString
import org.fossify.clock.extensions.getFormattedDate
import org.fossify.clock.extensions.getFormattedTime
import org.fossify.clock.models.AlarmEvent
import org.fossify.clock.models.MyTimeZone
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**
 * The clock tab: ticks every second on second boundaries (only while something
 * collects it), the closest enabled alarm string, and the selected world-clock
 * time zones rendered like the old TimeZonesAdapter did (zone time, day label
 * when the day differs, offset to local time).
 */
class ClockViewModel(app: Application) : AndroidViewModel(app) {
    private val context = app.applicationContext

    /** The current time in epoch millis, ticking exactly on second boundaries. */
    val now: StateFlow<Long> = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(1000 - System.currentTimeMillis() % 1000)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), System.currentTimeMillis())

    private val _nextAlarm = MutableStateFlow("")

    /** The closest enabled alarm as "Day HH:mm" (empty when there is none). */
    val nextAlarm: StateFlow<String> = _nextAlarm

    private val selectedZones = MutableStateFlow<List<MyTimeZone>>(emptyList())

    /** The selected time zones, recomputed on every tick of [now]. */
    val timeZones: StateFlow<List<TimeZoneUi>> = combine(now, selectedZones) { millis, zones ->
        zones.map { toTimeZoneUi(it, millis) }
    }.flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val use24h: Boolean
        get() = context.config.use24HourFormat

    private val _faceStyle = MutableStateFlow(context.config.clockFaceStyle)

    /** The hero clock face: [CLOCK_FACE_DIGITAL] or [CLOCK_FACE_ANALOGUE], persisted in [org.fossify.clock.helpers.Config]. */
    val faceStyle: StateFlow<Int> = _faceStyle

    /** Switches the hero clock face and persists the choice. */
    fun toggleFaceStyle() {
        val newStyle = if (_faceStyle.value == CLOCK_FACE_ANALOGUE) CLOCK_FACE_DIGITAL else CLOCK_FACE_ANALOGUE
        context.config.clockFaceStyle = newStyle
        _faceStyle.value = newStyle
    }

    init {
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAlarmRefresh(event: AlarmEvent.Refresh) {
        refreshNextAlarm()
    }

    override fun onCleared() {
        super.onCleared()
        EventBus.getDefault().unregister(this)
    }

    fun refresh() {
        refreshNextAlarm()
        refreshTimeZones()
    }

    /** The DB query already runs off the main thread; the callback lands on main. */
    fun refreshNextAlarm() {
        context.getClosestEnabledAlarmString { _nextAlarm.value = it }
    }

    fun refreshTimeZones() {
        viewModelScope.launch {
            val zones = withContext(Dispatchers.IO) {
                val selectedIds = context.config.selectedTimeZones.map { it.toInt() }
                context.getAllTimeZonesModified().filter { selectedIds.contains(it.id) }
            }
            selectedZones.value = zones
        }
    }

    private fun toTimeZoneUi(timeZone: MyTimeZone, nowMillis: Long): TimeZoneUi {
        val currTimeZone = TimeZone.getTimeZone(timeZone.zoneName)
        val calendar = Calendar.getInstance(currTimeZone).apply { timeInMillis = nowMillis }
        val offset = getOffsetWithDst(currTimeZone, nowMillis)
        val passedSeconds = ((calendar.timeInMillis + offset) / 1000).toInt()
        val formattedTime = context.getFormattedTime(passedSeconds, false, false).toString()
        val formattedDate = context.getFormattedDate(calendar)

        var time = formattedTime
        var amPm: String? = null
        if (!use24h) {
            when {
                formattedTime.endsWith(" AM") -> {
                    time = formattedTime.dropLast(3)
                    amPm = "AM"
                }
                formattedTime.endsWith(" PM") -> {
                    time = formattedTime.dropLast(3)
                    amPm = "PM"
                }
            }
        }

        val todayString = context.getFormattedDate(Calendar.getInstance())
        val dayLabel = if (formattedDate != todayString) formattedDate else null

        val diff = offset - getOffsetWithDst(TimeZone.getDefault(), nowMillis)
        val offsetLabel = if (diff == 0) {
            context.getString(R.string.m3e_same_time)
        } else {
            formatOffset(diff)
        }

        return TimeZoneUi(
            id = timeZone.id,
            title = timeZone.title,
            time = time,
            amPm = amPm,
            dayLabel = dayLabel,
            offsetLabel = offsetLabel,
        )
    }

    /** Raw offset plus DST savings when active, the same way the old adapter computed it. */
    private fun getOffsetWithDst(timeZone: TimeZone, nowMillis: Long): Int {
        var offset = timeZone.rawOffset
        if (timeZone.inDaylightTime(Date(nowMillis))) {
            offset += timeZone.dstSavings
        }
        return offset
    }

    private fun formatOffset(diffMillis: Int): String {
        val sign = if (diffMillis > 0) "+" else "−"
        val abs = kotlin.math.abs(diffMillis.toLong())
        val hours = abs / 3_600_000
        val minutes = (abs / 60_000) % 60
        val value = if (minutes == 0L) {
            "$hours h"
        } else {
            "$hours:${minutes.toString().padStart(2, '0')} h"
        }
        return "$sign$value"
    }
}

/** One rendered row of the world clock list. */
data class TimeZoneUi(
    val id: Int,
    val title: String,
    val time: String,
    val amPm: String?,
    val dayLabel: String?,
    val offsetLabel: String,
)
