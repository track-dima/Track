package it.polimi.dima.track.common.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import it.polimi.dima.track.common.ext.calculateTotalTime
import it.polimi.dima.track.common.ext.parseTrainingSteps
import it.polimi.dima.track.model.Training
import java.util.TimeZone

fun copyToClipboard(context: Context, text: String, label: String = "") {
  val clipboardManager =
    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
  val clip = ClipData.newPlainText(label, text)
  clipboardManager.setPrimaryClip(clip)
}

fun sendIntent(context: Context, text: String) {
  val sendIntent: Intent = Intent().apply {
    action = Intent.ACTION_SEND
    putExtra(Intent.EXTRA_TEXT, text)
    type = "text/plain"
  }
  val shareIntent = Intent.createChooser(sendIntent, null)
  context.startActivity(shareIntent)
}

fun addToCalendar(context: Context, training: Training) {
  val startMillis: Long = getStartTimeInMillis(training)
  val roundedEndTimeMillis = getEndTimeInMillis(training, startMillis)
  val description = getCalendarDescription(training)

  val intent = Intent(Intent.ACTION_INSERT)
    .setData(CalendarContract.Events.CONTENT_URI)
    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, roundedEndTimeMillis)
    .putExtra(CalendarContract.Events.TITLE, training.title)
    .putExtra(CalendarContract.Events.DESCRIPTION, description)

  context.startActivity(intent)
}

private fun getCalendarDescription(training: Training): String {
  return (if (training.description.isNotBlank()) training.description + "\n\n" else "") +
      training.parseTrainingSteps()
}

private fun getEndTimeInMillis(training: Training, startMillis: Long): Long {
  val trainingTimeInSeconds = training.calculateTotalTime()
  val trainingTimeInMillis = trainingTimeInSeconds * 1000

  val halfHourInMillis = 30 * 60 * 1000
  return (startMillis + trainingTimeInMillis + halfHourInMillis) / halfHourInMillis * halfHourInMillis
}

private fun getStartTimeInMillis(training: Training): Long {
  val currentTime = System.currentTimeMillis()
  val timeZone = TimeZone.getDefault()

  val timeMillis = if (training.dueTime != null) {
    training.dueTime["hour"]!! * 60 * 60 * 1000 + training.dueTime["minute"]!! * 60 * 1000
  } else 18 * 60 * 60 * 1000

  val startMillis: Long =
    if (training.dueDate != null) {
      convertLocalToUtc(training.dueDate.time, timeZone) + timeMillis
    } else {
      val currentTimeUtc = convertLocalToUtc(currentTime, timeZone)
      currentTimeUtc - (currentTimeUtc % 86400000) + timeMillis
    }
  return startMillis
}

private fun convertLocalToUtc(localTimeMillis: Long, timeZone: TimeZone): Long {
  val offset = timeZone.getOffset(localTimeMillis)
  return localTimeMillis - offset
}
