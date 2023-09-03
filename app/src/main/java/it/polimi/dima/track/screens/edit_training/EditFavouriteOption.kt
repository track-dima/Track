package it.polimi.dima.track.screens.edit_training

enum class EditFavouriteOption {
  Yes,
  No;

  companion object {
    fun getByCheckedState(checkedState: Boolean?): EditFavouriteOption {
      val hasFavourite = checkedState ?: false
      return if (hasFavourite) Yes else No
    }

    fun getBooleanValue(favouriteOption: String): Boolean {
      return favouriteOption == Yes.name
    }

    fun getOptions(): List<String> {
      val options = mutableListOf<String>()
      values().forEach { favouriteOption -> options.add(favouriteOption.name) }
      return options
    }
  }
}
