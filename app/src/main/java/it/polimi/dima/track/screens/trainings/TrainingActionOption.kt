package it.polimi.dima.track.screens.trainings

enum class TrainingActionOption(val title: String) {
  EditTraining("Edit training"),
  ToggleFlag("Toggle flag"),
  DeleteTask("Delete training");

  companion object {
    fun getByTitle(title: String): TrainingActionOption {
      values().forEach { action -> if (title == action.title) return action }

      return EditTraining
    }

    fun getOptions(hasEditOption: Boolean): List<String> {
      val options = mutableListOf<String>()
      values().forEach { trainingAction ->
        if (hasEditOption || trainingAction != EditTraining) {
          options.add(trainingAction.title)
        }
      }
      return options
    }
  }
}
