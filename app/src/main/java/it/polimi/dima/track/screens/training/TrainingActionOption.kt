package it.polimi.dima.track.screens.training

enum class TrainingActionOption(val title: String) {
  EditTraining("Edit training"),
  CopyTraining("Copy training"),
  Share("Share"),
  ToggleFavourite("Toggle favourite"),
  DuplicateTraining("Duplicate training"),
  DeleteTask("Delete training");

  companion object {
    fun getByTitle(title: String): TrainingActionOption {
      values().forEach { action -> if (title == action.title) return action }

      return EditTraining
    }

    fun getOptions(reduced: Boolean): List<String> {
      val options = mutableListOf<String>()
      values().forEach { trainingAction ->
        if (!reduced || trainingAction !in listOf(ToggleFavourite, EditTraining)) {
          options.add(trainingAction.title)
        }
      }
      return options
    }
  }
}
