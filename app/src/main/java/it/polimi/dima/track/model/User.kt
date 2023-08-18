package it.polimi.dima.track.model

data class User(
    val id: String = "",
    val isAnonymous: Boolean = true,
    val name: String = "",
    val specialty: String = "",
)
