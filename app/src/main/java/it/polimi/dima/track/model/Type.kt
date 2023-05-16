package it.polimi.dima.track.model

enum class Type {
  None,
  Track,
  Cross,
  Street,
  Other;

  companion object {
    fun getByName(name: String?): Type {
      values().forEach { type -> if (name == type.name) return type }

      return None
    }

    fun getOptions(): List<String> {
      val options = mutableListOf<String>()
      values().forEach { type -> options.add(type.name) }
      return options
    }
  }
}
