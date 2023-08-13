package it.polimi.dima.track.common.ext

import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Date?.getDay(default: String = ""): String {
  val calendar = Calendar.getInstance()
  return if (this != null) {
    calendar.time = this
    calendar.get(Calendar.DAY_OF_MONTH).toString()
  } else default
}

fun Date?.getDayName(default: String = ""): String {
  val calendar = Calendar.getInstance()
  return if (this != null) {
    calendar.time = this
    calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) ?: default
  } else default
}

fun Date?.isToday(): Boolean {
  val calendar = Calendar.getInstance()
  return if (this != null) {
    calendar.time = this
    val today = Calendar.getInstance()
    today.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR) &&
        today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
  } else false
}