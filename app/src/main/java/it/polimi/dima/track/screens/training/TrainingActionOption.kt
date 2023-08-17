package it.polimi.dima.track.screens.training

enum class TrainingActionOption(val title: String) {
  EditTraining("Edit training"),
  CopyTraining("Copy training"),
  Share("Share link"),
  ToggleFavourite("Toggle favourite"),
  DuplicateTraining("Duplicate training"),
  DeleteTask("Delete training"),
  AddToCalendar("Add to calendar");

  companion object {
    fun getByTitle(title: String): TrainingActionOption {
      values().forEach { action -> if (title == action.title) return action }

      return EditTraining
    }

    fun getOptions(reduced: Boolean): List<String> {
      val options = mutableListOf<String>()
      values().forEach { trainingAction ->
        if (reduced) {
          if (trainingAction !in listOf(ToggleFavourite, EditTraining)) {
            options.add(trainingAction.title)
          }
        } else if (trainingAction !in listOf(AddToCalendar)) {
          options.add(trainingAction.title)
        }
      }
      return options
    }
  }
}
