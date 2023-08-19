package it.polimi.dima.track.common.ext

import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Date?.getWeekInterval(default: String = ""): String {
  val calendar = Calendar.getInstance()
  return if (this != null) {
    calendar.time = this
    val startOfWeek = calendar.clone() as Calendar
    startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.firstDayOfWeek)
    val endOfWeek = calendar.clone() as Calendar
    endOfWeek.set(Calendar.DAY_OF_WEEK, endOfWeek.firstDayOfWeek)
    endOfWeek.add(Calendar.DAY_OF_WEEK, 6)

    val startMonth =
      startOfWeek.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
    val endMonth =
      endOfWeek.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())

    return if (startMonth == endMonth) {
      "${startOfWeek.get(Calendar.DAY_OF_MONTH)}-${endOfWeek.get(Calendar.DAY_OF_MONTH)} " +
          "$startMonth ${startOfWeek.get(Calendar.YEAR)}"
    } else {
      "${startOfWeek.get(Calendar.DAY_OF_MONTH)} $startMonth - " +
          "${endOfWeek.get(Calendar.DAY_OF_MONTH)} $endMonth ${startOfWeek.get(Calendar.YEAR)}"
    }
  } else default
}

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

fun Date?.isThisWeek(): Boolean {
  val calendar = Calendar.getInstance()
  return if (this != null) {
    calendar.time = this
    val today = Calendar.getInstance()
    today.get(Calendar.WEEK_OF_YEAR) == calendar.get(Calendar.WEEK_OF_YEAR) &&
        today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
  } else false
}

fun Date?.isThisMonth(): Boolean {
  val calendar = Calendar.getInstance()
  return if (this != null) {
    calendar.time = this
    val today = Calendar.getInstance()
    today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
        today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
  } else false
}