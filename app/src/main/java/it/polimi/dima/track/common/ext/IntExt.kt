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