package com.mozzarelly.homerodeo.data.api.fake

import com.mozzarelly.homerodeo.data.api.AlarmApi
import com.mozzarelly.homerodeo.data.model.AlarmData
import com.mozzarelly.homerodeo.data.model.Day
import com.mozzarelly.homerodeo.data.model.Time
import com.mozzarelly.homerodeo.data.model.Triggered
import com.mozzarelly.homerodeo.util.ApiResponse
import com.mozzarelly.homerodeo.util.ApiResult
import kotlinx.coroutines.delay

class FakeAlarmApi : AlarmApi {

    private val days = listOf(
        Day(num = 0,  index = 0, time = Time("7", "30"), type = 'f', canonicalName = "monday_odd"),
        Day(num = 1,  index = 1, time = Time("7", "30"), type = 'f', canonicalName = "tuesday_odd"),
        Day(num = 2,  index = 2, time = Time("7", "30"), type = 'f', canonicalName = "wednesday_odd"),
        Day(num = 3,  index = 3, time = Time("7", "30"), type = 'f', canonicalName = "thursday_odd"),
        Day(num = 4,  index = 4, time = Time("7", "30"), type = 'f', canonicalName = "friday_odd"),
        Day(num = 5,  index = 5, time = null,            type = 'f', canonicalName = "saturday_odd",  disabled = true),
        Day(num = 6,  index = 6, time = null,            type = 'f', canonicalName = "sunday_odd",    disabled = true),
        Day(num = 7,  index = 0, time = Time("7", "30"), type = 'f', canonicalName = "monday_even"),
        Day(num = 8,  index = 1, time = Time("7", "30"), type = 'f', canonicalName = "tuesday_even"),
        Day(num = 9,  index = 2, time = Time("7", "30"), type = 'f', canonicalName = "wednesday_even"),
        Day(num = 10, index = 3, time = Time("7", "30"), type = 'f', canonicalName = "thursday_even"),
        Day(num = 11, index = 4, time = Time("7", "30"), type = 'f', canonicalName = "friday_even"),
        Day(num = 12, index = 5, time = null,            type = 'f', canonicalName = "saturday_even", disabled = true),
        Day(num = 13, index = 6, time = null,            type = 'f', canonicalName = "sunday_even",   disabled = true),
    )

    private var alarm = AlarmData(
        on = false,
        nextNum = 1,
        override = null,
        lastTriggered = Triggered(
            day = "20250623",
            time = Time("7", "30"),
            action = "rung",
            overridden = false,
            disabled = false,
            today = true,
        ),
        days = days,
        today = days[0],
    )

    override suspend fun getAlarm(): ApiResponse<AlarmData> {
        delay(500)
        return ApiResult(alarm)
    }

    override suspend fun setAlarm(alarm: AlarmData): AlarmData {
        delay(500)
        this.alarm = alarm
        return alarm
    }

    override suspend fun saveSetting(day: String, set: String) {
        delay(500)
        val dayNum = day.toIntOrNull() ?: return
        alarm = alarm.copy(days = alarm.days.map {
            if (it.num == dayNum) it.copy(time = Time.fromString(set)) else it
        })
    }

    override suspend fun saveOverride(day: String, set: String) {
        delay(500)
        val dayNum = day.toIntOrNull() ?: return
        alarm = alarm.copy(days = alarm.days.map {
            if (it.num == dayNum)
                it.copy(time = runCatching { Time.fromString(set) }.getOrNull(), overridden = true)
            else
                it
        })
    }

    override suspend fun saveOverrideRange(set: String) {
        delay(500)
    }

    override suspend fun disable(days: Int) {
        delay(500)
        alarm = alarm.copy(days = alarm.days.map {
            if (it.num == days) it.copy(disabled = true) else it
        })
    }

    override suspend fun undisable(days: String) {
        delay(500)
        val dayNum = days.toIntOrNull() ?: return
        alarm = alarm.copy(days = alarm.days.map {
            if (it.num == dayNum) it.copy(disabled = false) else it
        })
    }
}
