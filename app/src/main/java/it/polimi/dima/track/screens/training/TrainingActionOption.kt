package it.polimi.dima.track.screens.training

import it.polimi.dima.track.AGENDA_SCREEN
import it.polimi.dima.track.TRAINING_SCREEN

enum class TrainingActionOption(val title: String) {
  EditTraining("Edit training"),
  CopyTraining("Copy training"),
  ShareLink("Share link"),
  ToggleFavourite("Toggle favourite"),
  DuplicateTraining("Duplicate training"),
  DeleteTraining("Delete training"),
  AddToCalendar("Add to calendar");

  companion object {
    fun getByTitle(title: String): TrainingActionOption {
      values().forEach { action -> if (title == action.title) return action }
      return EditTraining
    }

    fun getOptions(screen: String): List<String> {
      val options = mutableListOf<String>()
      when (screen) {
        TRAINING_SCREEN -> {
          options.add(CopyTraining.title)
          options.add(ShareLink.title)
          options.add(DuplicateTraining.title)
          options.add(DeleteTraining.title)
          options.add(AddToCalendar.title)
        }

        AGENDA_SCREEN -> {
          options.add(EditTraining.title)
          options.add(CopyTraining.title)
          options.add(ShareLink.title)
          options.add(ToggleFavourite.title)
          options.add(DuplicateTraining.title)
          options.add(DeleteTraining.title)
        }

        else -> {
          options.add(EditTraining.title)
          options.add(CopyTraining.title)
          options.add(ShareLink.title)
          options.add(ToggleFavourite.title)
        }
      }
      return options
    }
  }
}
