package it.polimi.dima.track.common.ext

import it.polimi.dima.track.model.Training


fun Training?.hasDueDate(): Boolean {
  return (this?.dueDate != null && this.dueDateString.isNotBlank())
}

fun Training?.hasDueTime(): Boolean {
  return (this?.dueTime != null && this.dueTimeString.isNotBlank())
}
