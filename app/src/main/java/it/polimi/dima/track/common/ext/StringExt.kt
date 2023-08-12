package it.polimi.dima.track.common.ext

import android.util.Patterns
import java.util.regex.Pattern

private const val MIN_PASS_LENGTH = 6
private const val PASS_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$"

fun String.isValidEmail(): Boolean {
  return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
  return this.isNotBlank() &&
      this.length >= MIN_PASS_LENGTH &&
      Pattern.compile(PASS_PATTERN).matcher(this).matches()
}

fun String.passwordMatches(repeated: String): Boolean {
  return this == repeated
}

fun String.idFromParameter(): String {
  return this.substring(1, this.length - 1)
}

fun String.removeLeadingZeros(): String {
  val split = this.split(":")
  return if (split.size == 3) {
    // e.g. 00:12:34
    if (split[0].toInt() > 0)
      this
    else "${split[1].toInt()}" + ":${split[2]}"
  }
  else {
    // e.g. 00:12.34
    if (split[0].toInt() > 0)
      "${split[0].toInt()}" + ":${split[1]}"
    else {
      val split2 = split[1].split(".")
      "${split2[0].toInt()}" + ".${split2[1]}"
    }
  }
}

fun String.timeIsZero(): Boolean {
  val split = this.split(":")
  return if (split.size == 3) {
    split[0].toInt() == 0 && split[1].toInt() == 0 && split[2].toInt() == 0
  }
  else {
    // e.g. 00:12.34, or 00:12 min/km
    if (this.split(" ").size > 1) return split[0].toInt() == 0 && split[1].split(" ")[0].toInt() == 0
    val split2 = split[1].split(".")
    split[0].toInt() == 0 && split2[0].toInt() == 0 && split2[1].toInt() == 0
  }
}

fun String.timeToSeconds(): Int {
  if (this.isEmpty()) return 0
  val split = this.split(":")
  return if (split.size < 3) {
    split[0].toInt() * 60 + split[1].split(".")[0].toInt()
  } else split[0].toInt() * 3600 + split[1].toInt() * 60 + split[2].toInt()
}

fun String.extractCents(): Int {
  if (this.isEmpty()) return 0
  val split = this.split(".")
  if (split.size < 2) return 0
  return split[1].toInt()
}

fun String.paceToSeconds(): Int {
  if (this.isEmpty()) return 0
  val split = this.split(":")
  return split[0].toInt() * 60 + split[1].split(" ")[0].toInt()
}

fun String.extractPaceUnit(): String {
  if (this.isEmpty()) return "min/km"
  val split = this.split(" ")
  return split[1]
}
