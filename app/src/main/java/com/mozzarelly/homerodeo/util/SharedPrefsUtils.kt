package com.mozzarelly.homerodeo.util

import android.content.SharedPreferences

inline operator fun <reified T> SharedPreferences.get(key: String): T? {
  return if (contains(key)) {
    when (T::class) {
      String::class -> getString(key, null) as T
      Boolean::class -> getBoolean(key, false) as T
      Float::class -> getFloat(key, 0.0f) as T
      Int::class -> getInt(key, 0) as T
      Long::class -> getLong(key, 0L) as T
      else -> throw IllegalArgumentException(T::class.simpleName)
    }
  }
  else {
    null
  }
}

inline fun <reified T> SharedPreferences.getOrElse(key: String, orElse: () -> T): T {
  return if (contains(key)) {
    when (T::class) {
      String::class -> getString(key, null) as T
      Boolean::class -> getBoolean(key, false) as T
      Float::class -> getFloat(key, 0.0f) as T
      Int::class -> getInt(key, 0) as T
      Long::class -> getLong(key, 0L) as T
      else -> throw IllegalArgumentException(T::class.simpleName)
    }
  }
  else {
    orElse.invoke()
  }
}
