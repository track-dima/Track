package it.polimi.dima.track.common.ext

fun Int.secondsToHhMmSs(): String {
  val hours = this / 3600
  val minutes = (this % 3600) / 60
  val remainingSeconds = this % 60

  return String.format("%d:%02d:%02d", hours, minutes, remainingSeconds).removeLeadingZeros()
}

fun Int.secondsToHhMm(): String {
  val hours = this / 3600
  val minutes = (this % 3600) / 60

  return String.format("%d:%02d", hours, minutes)
}

fun Int.toClockPattern(): String {
  return if (this < 10) "0$this" else "$this"
}

fun Int.distanceToSeconds(distanceUnit: String): Int {
  // Convert the distance to meters
  val dist = when (distanceUnit) {
    "km" -> this * 1000
    "mi" -> (this * 1609.344).toInt()
    else -> this
  }
  // Convert the distance to seconds (5 min/km)
  return dist * 5 * 60 / 1000
}

fun Int.distanceToMeters(distanceUnit: String): Int {
  return when (distanceUnit) {
    "km" -> this * 1000
    "mi" -> (this * 1609.344).toInt()
    else -> this
  }
}

fun Int.formatTimeRecover(): String {
  if (this < 60) return "$this''"
  if (this % 60 == 0) return "${this / 60}'"
  val minutes = this / 60
  val seconds = this % 60
  return String.format("%d:%02d''", minutes, seconds)
}