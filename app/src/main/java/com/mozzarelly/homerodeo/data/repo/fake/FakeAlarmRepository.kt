package com.mozzarelly.homerodeo.data.repo.fake

/*
class FakeAlarmRepository : AlarmRepository {

    private val fakeAlarm = Alarm(
      on = false,
      nextNum = 0,
      override = null,
      lastTriggered = Triggered(
        day = "20250623",
        time = Time("7", "30"),
        action = "rung",
        overridden = false,
        disabled = false,
        today = true,
      ),
      days = listOf(
        Day(num = 0, index = 0, time = Time("7", "30"), type = 'f', canonicalName = "monday_odd"),
        Day(num = 1, index = 1, time = Time("7", "30"), type = 'f', canonicalName = "tuesday_odd"),
        Day(
          num = 2,
          index = 2,
          time = Time("7", "30"),
          type = 'f',
          canonicalName = "wednesday_odd"
        ),
        Day(num = 3, index = 3, time = Time("7", "30"), type = 'f', canonicalName = "thursday_odd"),
        Day(num = 4, index = 4, time = Time("7", "30"), type = 'f', canonicalName = "friday_odd"),
        Day(
          num = 5,
          index = 5,
          time = null,
          type = 'f',
          canonicalName = "saturday_odd",
          disabled = true
        ),
        Day(
          num = 6,
          index = 6,
          time = null,
          type = 'f',
          canonicalName = "sunday_odd",
          disabled = true
        ),
        Day(num = 7, index = 0, time = Time("7", "30"), type = 'f', canonicalName = "monday_even"),
        Day(num = 8, index = 1, time = Time("7", "30"), type = 'f', canonicalName = "tuesday_even"),
        Day(
          num = 9,
          index = 2,
          time = Time("7", "30"),
          type = 'f',
          canonicalName = "wednesday_even"
        ),
        Day(
          num = 10,
          index = 3,
          time = Time("7", "30"),
          type = 'f',
          canonicalName = "thursday_even"
        ),
        Day(num = 11, index = 4, time = Time("7", "30"), type = 'f', canonicalName = "friday_even"),
        Day(
          num = 12,
          index = 5,
          time = null,
          type = 'f',
          canonicalName = "saturday_even",
          disabled = true
        ),
        Day(
          num = 13,
          index = 6,
          time = null,
          type = 'f',
          canonicalName = "sunday_even",
          disabled = true
        ),
      ),
      today = Day(
        num = 0,
        index = 0,
        time = Time("7", "30"),
        type = 'f',
        canonicalName = "monday_odd"
      ),
    )

    override val currentState = MutableStateFlow<ApiResponse<Alarm>?>(ApiResult(fakeAlarm))

    override val alarmFlow: Flow<ApiResponse<Alarm>> = flow {
      currentState.collect { if (it != null) emit(it) }
    }

    override suspend fun saveDay(day: String, setting: String, saveAsSetting: Boolean) {
        // no-op in fake
    }

    override suspend fun refresh() {
        currentState.emit(ApiResult(fakeAlarm))
    }
}*/
