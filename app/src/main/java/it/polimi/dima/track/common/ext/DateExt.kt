package it.polimi.dima.track.common.ext

import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Date?.getWeekInterval(default: String = ""): String {
  val calendar = Calendar.getInstance()
  return if (this != null) {
    calendar.time = this
    val startOfWeek = calendar.clone() as Calendar
    startOfWeek[Calendar.DAY_OF_WEEK] = startOfWeek.firstDayOfWeek
    val endOfWeek = calendar.clone() as Calendar
    endOfWeek[Calendar.DAY_OF_WEEK] = endOfWeek.firstDayOfWeek
    endOfWeek.add(Calendar.DAY_OF_WEEK, 6)
    val startMonth =
      startOfWeek.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
    val endMonth =
      endOfWeek.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
    val startYear = startOfWeek[Calendar.YEAR]
    val endYear = endOfWeek[Calendar.YEAR]

    return if (startMonth == endMonth) {
      "${startOfWeek[Calendar.DAY_OF_MONTH]}-${endOfWeek[Calendar.DAY_OF_MONTH]} " +
          "$startMonth $startYear"
    } else {
      if (startYear == endYear) {
        "${startOfWeek[Calendar.DAY_OF_MONTH]} $startMonth - " +
            "${endOfWeek[Calendar.DAY_OF_MONTH]} $endMonth $startYear"
      } else {
        "${startOfWeek[Calendar.DAY_OF_MONTH]} $startMonth $startYear - " +
            "${endOfWeek[Calendar.DAY_OF_MONTH]} $endMonth $endYear"
      }
    }
  } else default
}

fun Date?.getDay(default: String = ""): String {
  val calendar = Calendar.getInstance()
  return if (this != null) {
    calendar.time = this
    calendar[Calendar.DAY_OF_MONTH].toString()
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
    today[Calendar.DAY_OF_YEAR] == calendar[Calendar.DAY_OF_YEAR] &&
        today[Calendar.YEAR] == calendar[Calendar.YEAR]
  } else false
}

fun Date?.isThisWeek(): Boolean {
  val calendar = Calendar.getInstance()
  return if (this != null) {
    calendar.time = this
    val today = Calendar.getInstance()
    today[Calendar.WEEK_OF_YEAR] == calendar[Calendar.WEEK_OF_YEAR] &&
        today[Calendar.YEAR] == calendar[Calendar.YEAR]
  } else false
}

fun Date?.isThisMonth(): Boolean {
  val calendar = Calendar.getInstance()
  return if (this != null) {
    calendar.time = this
    val today = Calendar.getInstance()
    today[Calendar.MONTH] == calendar[Calendar.MONTH] &&
        today[Calendar.YEAR] == calendar[Calendar.YEAR]
  } else false
}