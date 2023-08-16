package it.polimi.dima.track.common.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import it.polimi.dima.track.common.ext.calculateTotalTime
import it.polimi.dima.track.model.Training

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

fun shareOnCalendar(context: Context, training: Training) {
  val currentTime = System.currentTimeMillis()
  val startMillis: Long =
    (if (training.dueDate != null) training.dueDate.time else (currentTime - (currentTime % 86400000))) +
        if (training.dueTime != null) training.dueTime["hour"]!! * 60 * 60 * 1000 + training.dueTime["minute"]!! * 60 * 1000
        else 18 * 60 * 60 * 1000

  val totalTimeInSeconds = training.calculateTotalTime()
  val totalTimeInMilliseconds = totalTimeInSeconds * 1000

  val halfHourInMillis = 30 * 60 * 1000
  val roundedEndTimeMillis = (startMillis + totalTimeInMilliseconds + halfHourInMillis) / halfHourInMillis * halfHourInMillis


  val intent = Intent(Intent.ACTION_INSERT)
    .setData(CalendarContract.Events.CONTENT_URI)
    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, roundedEndTimeMillis)
    .putExtra(CalendarContract.Events.TITLE, training.title)
    .putExtra(CalendarContract.Events.DESCRIPTION, training.description)
  context.startActivity(intent)
}