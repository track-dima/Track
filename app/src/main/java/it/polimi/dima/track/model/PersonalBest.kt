package it.polimi.dima.track.model

import com.google.firebase.firestore.DocumentId

data class PersonalBest(
  @DocumentId val id: String = "",
  val type: String = "",
  val distance: Int = 0,
  val duration: Int = 0,
  val result: String = "",
  val trainingId: String = "",
)
