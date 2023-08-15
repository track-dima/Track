package it.polimi.dima.track.common.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent

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